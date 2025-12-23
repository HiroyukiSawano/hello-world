package org.example.myjweb.controller;

import java.util.List;

import org.example.myjweb.controller.dto.RequestRunDto;
import org.example.myjweb.entity.RequestRun;
import org.example.myjweb.service.RequestExecutionService;
import org.example.myjweb.service.RequestRunService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求执行记录接口，负责记录和查询请求运行结果。
 */
@RestController
@RequestMapping("/api")
public class RequestRunController {

    private final RequestExecutionService requestExecutionService;
    private final RequestRunService requestRunService;

    public RequestRunController(RequestExecutionService requestExecutionService, RequestRunService requestRunService) {
        this.requestExecutionService = requestExecutionService;
        this.requestRunService = requestRunService;
    }

    @PostMapping("/request-versions/{requestVersionId}/execute")
    public RequestRunDto.DetailResponse execute(@PathVariable Long requestVersionId,
                                                @RequestBody(required = false) RequestRunDto.ExecuteRequest request) {
        Long environmentId = request == null ? null : request.environmentId();
        return toDetailResponse(requestExecutionService.executeAndRecord(requestVersionId, environmentId));
    }

    @PostMapping("/request-versions/{requestVersionId}/runs")
    public RequestRunDto.Response record(@PathVariable Long requestVersionId, @RequestBody RequestRunDto.Request request) {
        RequestRun run = new RequestRun();
        run.setRequestVersionId(requestVersionId);
        run.setEnvironmentId(request.environmentId());
        run.setStatusCode(request.statusCode());
        run.setDurationMs(request.durationMs());
        run.setSuccess(request.success());
        run.setResponseHeadersJson(request.responseHeadersJson());
        run.setResponseBody(request.responseBody());
        run.setResponseMime(request.responseMime());
        run.setResponseCharset(request.responseCharset());
        run.setResponseSize(request.responseSize());
        return toResponse(requestRunService.record(run));
    }

    @GetMapping("/request-runs/{id}")
    public ResponseEntity<RequestRunDto.Response> find(@PathVariable Long id) {
        return requestRunService.findById(id)
                .map(RequestRunController::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping("/request-versions/{requestVersionId}/runs")
    public List<RequestRunDto.Response> listByVersion(@PathVariable Long requestVersionId) {
        return requestRunService.listByRequestVersionId(requestVersionId).stream()
                .map(RequestRunController::toResponse)
                .toList();
    }

    @GetMapping("/environments/{environmentId}/runs")
    public List<RequestRunDto.Response> listByEnvironment(@PathVariable Long environmentId) {
        return requestRunService.listByEnvironmentId(environmentId).stream()
                .map(RequestRunController::toResponse)
                .toList();
    }

    @DeleteMapping("/request-runs/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = requestRunService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private static RequestRunDto.Response toResponse(RequestRun run) {
        return new RequestRunDto.Response(run.getId(), run.getRequestVersionId(), run.getEnvironmentId(),
                run.getStatusCode(), run.getDurationMs(), run.getSuccess(), run.getResponseHeadersJson(),
                run.getResponseMime(), run.getResponseCharset(), run.getResponseSize(), run.getCreatedAt());
    }

    private static RequestRunDto.DetailResponse toDetailResponse(RequestRun run) {
        return new RequestRunDto.DetailResponse(run.getId(), run.getRequestVersionId(), run.getEnvironmentId(),
                run.getStatusCode(), run.getDurationMs(), run.getSuccess(), run.getResponseHeadersJson(),
                run.getResponseMime(), run.getResponseCharset(), run.getResponseSize(), run.getCreatedAt(),
                run.getResponseBody());
    }
}
