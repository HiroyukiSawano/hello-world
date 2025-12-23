package org.example.myjweb.controller;

import org.example.myjweb.controller.dto.ApiRequestDto;
import org.example.myjweb.controller.dto.RequestVersionDto;
import org.example.myjweb.entity.ApiRequest;
import org.example.myjweb.entity.RequestVersion;
import org.example.myjweb.service.ApiRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求接口，负责创建与删除请求。
 */
@RestController
@RequestMapping("/api/requests")
public class ApiRequestController {

    private final ApiRequestService apiRequestService;

    public ApiRequestController(ApiRequestService apiRequestService) {
        this.apiRequestService = apiRequestService;
    }

    @PostMapping
    public RequestVersionDto.Response create(@RequestBody RequestVersionDto.Request request) {
        RequestVersion draft = buildDraft(request);
        RequestVersion created = apiRequestService.createRequestWithVersion(draft);
        return RequestVersionController.toResponse(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiRequestDto.Response> find(@PathVariable Long id) {
        return apiRequestService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = apiRequestService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private ApiRequestDto.Response toResponse(ApiRequest request) {
        return new ApiRequestDto.Response(request.getId(), request.getCreatedAt());
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
