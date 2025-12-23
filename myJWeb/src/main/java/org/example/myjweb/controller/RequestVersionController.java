package org.example.myjweb.controller;

import java.util.List;

import org.example.myjweb.controller.dto.RequestVersionDto;
import org.example.myjweb.entity.RequestVersion;
import org.example.myjweb.service.RequestVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求版本接口，提供版本管理与查询能力。
 */
@RestController
@RequestMapping("/api")
public class RequestVersionController {

    private final RequestVersionService requestVersionService;

    public RequestVersionController(RequestVersionService requestVersionService) {
        this.requestVersionService = requestVersionService;
    }

    @PostMapping("/requests/{requestId}/versions")
    public RequestVersionDto.Response createNext(@PathVariable Long requestId,
                                                 @RequestBody RequestVersionDto.Request request) {
        RequestVersion draft = buildDraft(request);
        RequestVersion created = requestVersionService.createNextVersion(requestId, draft);
        return toResponse(created);
    }

    @GetMapping("/requests/{requestId}/versions")
    public List<RequestVersionDto.Response> listByRequest(@PathVariable Long requestId) {
        return requestVersionService.listByRequestId(requestId).stream()
                .map(RequestVersionController::toResponse)
                .toList();
    }

    @GetMapping("/requests/{requestId}/versions/latest")
    public ResponseEntity<RequestVersionDto.Response> latest(@PathVariable Long requestId) {
        return requestVersionService.findLatestByRequestId(requestId)
                .map(RequestVersionController::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping("/request-versions/{id}")
    public ResponseEntity<RequestVersionDto.Response> find(@PathVariable Long id) {
        return requestVersionService.findById(id)
                .map(RequestVersionController::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PutMapping("/request-versions/{id}")
    public ResponseEntity<RequestVersionDto.Response> update(@PathVariable Long id,
                                                             @RequestBody RequestVersionDto.Request request) {
        RequestVersion update = buildDraft(request);
        update.setId(id);
        boolean updated = requestVersionService.update(update);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return requestVersionService.findById(id)
                .map(RequestVersionController::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @DeleteMapping("/request-versions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = requestVersionService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    static RequestVersionDto.Response toResponse(RequestVersion version) {
        return new RequestVersionDto.Response(version.getId(), version.getRequestId(), version.getVersion(),
                version.getName(), version.getFolderId(), version.getMethod(), version.getUrl(),
                version.getDescription(), version.getTimeoutMs(), version.getFollowRedirects(), version.getBodyType(),
                version.getHeadersJson(), version.getQueryParamsJson(), version.getCookiesJson(), version.getAuthJson(),
                version.getBodyJson(), version.getFormFieldsJson(), version.getBodyMime(), version.getGraphqlQuery(),
                version.getGraphqlVariablesJson(), version.getSoapAction(), version.getNote(), version.getCreatedAt());
    }

    private RequestVersion buildDraft(RequestVersionDto.Request request) {
        RequestVersion draft = new RequestVersion();
        draft.setName(request.name());
        draft.setFolderId(request.folderId());
        draft.setMethod(request.method());
        draft.setUrl(request.url());
        draft.setDescription(request.description());
        draft.setTimeoutMs(request.timeoutMs());
        draft.setFollowRedirects(request.followRedirects());
        draft.setBodyType(request.bodyType());
        draft.setHeadersJson(request.headersJson());
        draft.setQueryParamsJson(request.queryParamsJson());
        draft.setCookiesJson(request.cookiesJson());
        draft.setAuthJson(request.authJson());
        draft.setBodyJson(request.bodyJson());
        draft.setFormFieldsJson(request.formFieldsJson());
        draft.setBodyRaw(request.bodyRaw());
        draft.setBodyMime(request.bodyMime());
        draft.setGraphqlQuery(request.graphqlQuery());
        draft.setGraphqlVariablesJson(request.graphqlVariablesJson());
        draft.setSoapAction(request.soapAction());
        draft.setNote(request.note());
        return draft;
    }
}
