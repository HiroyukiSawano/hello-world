package org.example.myjweb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.example.myjweb.entity.Environment;
import org.example.myjweb.entity.EnvironmentVariable;
import org.example.myjweb.entity.RequestRun;
import org.example.myjweb.entity.RequestVersion;
import org.example.myjweb.repository.EnvironmentMapper;
import org.example.myjweb.repository.RequestVersionMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 请求执行服务，基于请求版本发起 HTTP/HTTPS 调用并记录结果。
 */
@Service
public class RequestExecutionService {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(.*?)}}");

    private final RequestVersionMapper requestVersionMapper;
    private final EnvironmentMapper environmentMapper;
    private final EnvironmentVariableService environmentVariableService;
    private final RequestRunService requestRunService;
    private final ObjectMapper objectMapper;

    public RequestExecutionService(RequestVersionMapper requestVersionMapper,
                                   EnvironmentMapper environmentMapper,
                                   EnvironmentVariableService environmentVariableService,
                                   RequestRunService requestRunService,
                                   ObjectMapper objectMapper) {
        this.requestVersionMapper = requestVersionMapper;
        this.environmentMapper = environmentMapper;
        this.environmentVariableService = environmentVariableService;
        this.requestRunService = requestRunService;
        this.objectMapper = objectMapper;
    }

    /**
     * 按请求版本执行一次 HTTP 调用并落库。
     *
     * @param requestVersionId 请求版本ID
     * @param environmentId    可选环境ID，用于变量替换
     * @return 执行记录
     */
    public RequestRun executeAndRecord(Long requestVersionId, Long environmentId) {
        ServiceValidator.requireId(requestVersionId, "请求版本ID不能为空");
        RequestVersion version = Optional.ofNullable(requestVersionMapper.selectById(requestVersionId))
                .orElseThrow(() -> new IllegalArgumentException("请求版本不存在"));
        Map<String, String> variables = loadVariables(environmentId);
        HttpClient client = buildClient(version);
        HttpRequest httpRequest = buildHttpRequest(version, variables);

        Instant start = Instant.now();
        try {
            HttpResponse<byte[]> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            int durationMs = Math.toIntExact(Duration.between(start, Instant.now()).toMillis());
            return requestRunService.record(buildRun(version, environmentId, response, durationMs));
        } catch (Exception ex) {
            int durationMs = Math.toIntExact(Duration.between(start, Instant.now()).toMillis());
            return requestRunService.record(buildFailedRun(version, environmentId, ex, durationMs));
        }
    }

    private Map<String, String> loadVariables(Long environmentId) {
        if (environmentId == null) {
            return Map.of();
        }
        ServiceValidator.requireId(environmentId, "环境ID不能为空");
        Environment environment = environmentMapper.selectById(environmentId);
        if (environment == null) {
            throw new IllegalArgumentException("环境不存在");
        }
        List<EnvironmentVariable> variables = environmentVariableService.listByEnvironmentId(environmentId);
        Map<String, String> result = new LinkedHashMap<>();
        for (EnvironmentVariable variable : variables) {
            if (variable.getIsEnabled() != null && variable.getIsEnabled() == 0) {
                continue;
            }
            result.put(variable.getName(), variable.getValue());
        }
        return result;
    }

    private HttpClient buildClient(RequestVersion version) {
        HttpClient.Redirect redirectPolicy = (version.getFollowRedirects() == null
                || version.getFollowRedirects() == 1) ? HttpClient.Redirect.ALWAYS : HttpClient.Redirect.NEVER;
        return HttpClient.newBuilder()
                .followRedirects(redirectPolicy)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private HttpRequest buildHttpRequest(RequestVersion version, Map<String, String> variables) {
        String method = version.getMethod() == null ? "GET" : version.getMethod().toUpperCase(Locale.ROOT);
        String resolvedUrl = appendQueryParams(resolvePlaceholders(version.getUrl(), variables),
                parseKeyValueMap(version.getQueryParamsJson(), variables));

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(resolvedUrl)).method(method, resolveBody(version, variables));
        if (version.getTimeoutMs() != null && version.getTimeoutMs() > 0) {
            builder.timeout(Duration.ofMillis(version.getTimeoutMs()));
        }

        Map<String, String> headers = parseKeyValueMap(version.getHeadersJson(), variables);
        String contentType = resolveContentType(version);
        if (headers.keySet().stream().noneMatch(key -> "content-type".equalsIgnoreCase(key))
                && StringUtils.hasText(contentType)) {
            builder.header("Content-Type", contentType);
        }
        headers.forEach(builder::header);

        String cookieHeader = buildCookieHeader(version.getCookiesJson(), variables);
        if (StringUtils.hasText(cookieHeader)) {
            builder.header("Cookie", cookieHeader);
        }

        return builder.build();
    }

    private HttpRequest.BodyPublisher resolveBody(RequestVersion version, Map<String, String> variables) {
        if (!supportsBody(version.getMethod())) {
            return HttpRequest.BodyPublishers.noBody();
        }
        if (version.getBodyRaw() != null && version.getBodyRaw().length > 0) {
            return HttpRequest.BodyPublishers.ofByteArray(version.getBodyRaw());
        }
        if (StringUtils.hasText(version.getBodyJson())) {
            String json = resolvePlaceholders(version.getBodyJson(), variables);
            return HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8);
        }
        if (StringUtils.hasText(version.getFormFieldsJson())) {
            Map<String, String> form = parseKeyValueMap(version.getFormFieldsJson(), variables);
            String encoded = encodeForm(form);
            return HttpRequest.BodyPublishers.ofString(encoded, StandardCharsets.UTF_8);
        }
        if (StringUtils.hasText(version.getGraphqlQuery())) {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("query", resolvePlaceholders(version.getGraphqlQuery(), variables));
            if (StringUtils.hasText(version.getGraphqlVariablesJson())) {
                payload.put("variables", readTree(version.getGraphqlVariablesJson()));
            }
            return HttpRequest.BodyPublishers.ofString(writeJson(payload), StandardCharsets.UTF_8);
        }
        return HttpRequest.BodyPublishers.noBody();
    }

    private boolean supportsBody(String method) {
        if (!StringUtils.hasText(method)) {
            return false;
        }
        return switch (method.toUpperCase(Locale.ROOT)) {
            case "POST", "PUT", "PATCH", "DELETE" -> true;
            default -> false;
        };
    }

    private Map<String, String> parseKeyValueMap(String json, Map<String, String> variables) {
        if (!StringUtils.hasText(json)) {
            return Map.of();
        }
        JsonNode root = readTree(json);
        Map<String, String> result = new LinkedHashMap<>();
        if (root.isObject()) {
            root.fields().forEachRemaining(entry ->
                    result.put(entry.getKey(), resolvePlaceholders(entry.getValue().asText(""), variables)));
            return result;
        }
        if (root.isArray()) {
            for (JsonNode node : root) {
                String name = node.path("name").asText("");
                boolean enabled = !node.has("enabled") && !node.has("isEnabled") || node.path("enabled").asBoolean(node.path("isEnabled").asBoolean(true));
                if (!enabled || !StringUtils.hasText(name)) {
                    continue;
                }
                String value = node.path("value").asText("");
                result.put(resolvePlaceholders(name, variables), resolvePlaceholders(value, variables));
            }
        }
        return result;
    }

    private String buildCookieHeader(String cookiesJson, Map<String, String> variables) {
        Map<String, String> cookies = parseKeyValueMap(cookiesJson, variables);
        if (cookies.isEmpty()) {
            return "";
        }
        List<String> pairs = new ArrayList<>();
        cookies.forEach((k, v) -> pairs.add(k + "=" + v));
        return String.join("; ", pairs);
    }

    private String appendQueryParams(String url, Map<String, String> queryParams) {
        if (queryParams.isEmpty()) {
            return url;
        }
        StringBuilder builder = new StringBuilder(url);
        builder.append(url.contains("?") ? "&" : "?");
        List<String> encoded = new ArrayList<>();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            encoded.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
                    + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        builder.append(String.join("&", encoded));
        return builder.toString();
    }

    private RequestRun buildRun(RequestVersion version, Long environmentId, HttpResponse<byte[]> response, int durationMs) {
        RequestRun run = new RequestRun();
        run.setRequestVersionId(version.getId());
        run.setEnvironmentId(environmentId);
        run.setStatusCode(response.statusCode());
        run.setDurationMs(durationMs);
        run.setSuccess(response.statusCode() >= 200 && response.statusCode() < 400 ? 1 : 0);
        run.setResponseHeadersJson(writeJson(response.headers().map()));
        run.setResponseBody(response.body());
        String contentType = response.headers().firstValue("Content-Type").orElse("");
        run.setResponseMime(parseMime(contentType));
        run.setResponseCharset(parseCharset(contentType));
        run.setResponseSize((long) response.body().length);
        return run;
    }

    private RequestRun buildFailedRun(RequestVersion version, Long environmentId, Exception exception, int durationMs) {
        RequestRun run = new RequestRun();
        run.setRequestVersionId(version.getId());
        run.setEnvironmentId(environmentId);
        run.setStatusCode(null);
        run.setDurationMs(durationMs);
        run.setSuccess(0);
        run.setResponseHeadersJson("{}");
        String message = Optional.ofNullable(exception.getMessage()).orElse(exception.getClass().getSimpleName());
        run.setResponseBody(message.getBytes(StandardCharsets.UTF_8));
        run.setResponseMime("text/plain");
        run.setResponseCharset(StandardCharsets.UTF_8.name());
        run.setResponseSize((long) run.getResponseBody().length);
        return run;
    }

    private String parseMime(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return "";
        }
        int index = contentType.indexOf(';');
        return index >= 0 ? contentType.substring(0, index).trim() : contentType.trim();
    }

    private String parseCharset(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return "";
        }
        for (String part : contentType.split(";")) {
            String trimmed = part.trim();
            if (trimmed.toLowerCase(Locale.ROOT).startsWith("charset=")) {
                return trimmed.substring(8);
            }
        }
        return StandardCharsets.UTF_8.name();
    }

    private String encodeForm(Map<String, String> form) {
        List<String> pairs = new ArrayList<>();
        form.forEach((k, v) -> pairs.add(URLEncoder.encode(k, StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(v, StandardCharsets.UTF_8)));
        return String.join("&", pairs);
    }

    private JsonNode readTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 解析失败", e);
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 序列化失败", e);
        }
    }

    private String resolvePlaceholders(String source, Map<String, String> variables) {
        if (!StringUtils.hasText(source) || variables.isEmpty()) {
            return source;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(source);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1).trim();
            String replacement = variables.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String resolveContentType(RequestVersion version) {
        if (StringUtils.hasText(version.getBodyMime())) {
            return version.getBodyMime();
        }
        if (StringUtils.hasText(version.getFormFieldsJson())) {
            return "application/x-www-form-urlencoded";
        }
        if (StringUtils.hasText(version.getGraphqlQuery()) || StringUtils.hasText(version.getBodyJson())) {
            return "application/json";
        }
        return "";
    }
}
