package org.example.myjweb.controller.dto;

import java.time.LocalDateTime;

/**
 * 请求执行记录接口模型。
 */
public final class RequestRunDto {

    private RequestRunDto() {
    }

    /**
     * 记录执行结果的请求体。
     */
    public record Request(Integer statusCode, Integer durationMs, Integer success, String responseHeadersJson,
                          byte[] responseBody, String responseMime, String responseCharset, Long responseSize,
                          Long environmentId) {
    }

    /**
     * 执行记录详情响应。
     */
    public record Response(Long id, Long requestVersionId, Long environmentId, Integer statusCode, Integer durationMs,
                           Integer success, String responseHeadersJson, String responseMime, String responseCharset,
                           Long responseSize, LocalDateTime createdAt) {
    }
}
