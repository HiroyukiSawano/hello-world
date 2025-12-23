package org.example.myjweb.controller;

import java.util.List;

import org.example.myjweb.controller.dto.RequestFileDto;
import org.example.myjweb.entity.RequestFile;
import org.example.myjweb.service.RequestFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求附件接口，管理请求版本的文件列表。
 */
@RestController
@RequestMapping("/api")
public class RequestFileController {

    private final RequestFileService requestFileService;

    public RequestFileController(RequestFileService requestFileService) {
        this.requestFileService = requestFileService;
    }

    @PostMapping("/request-versions/{requestVersionId}/files")
    public RequestFileDto.Response create(@PathVariable Long requestVersionId,
                                          @RequestBody RequestFileDto.Request request) {
        RequestFile file = new RequestFile();
        file.setRequestVersionId(requestVersionId);
        file.setFieldName(request.fieldName());
        file.setFileName(request.fileName());
        file.setContentType(request.contentType());
        file.setSize(request.size());
        file.setContent(request.content());
        file.setEnabled(request.enabled());
        file.setSortOrder(request.sortOrder());
        return toResponse(requestFileService.create(file));
    }

    @GetMapping("/request-versions/{requestVersionId}/files")
    public List<RequestFileDto.Response> listByVersion(@PathVariable Long requestVersionId) {
        return requestFileService.listByRequestVersionId(requestVersionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/request-files/{id}")
    public ResponseEntity<RequestFileDto.Response> find(@PathVariable Long id) {
        return requestFileService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @DeleteMapping("/request-files/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = requestFileService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private RequestFileDto.Response toResponse(RequestFile file) {
        return new RequestFileDto.Response(file.getId(), file.getRequestVersionId(), file.getFieldName(),
                file.getFileName(), file.getContentType(), file.getSize(), file.getEnabled(), file.getSortOrder(),
                file.getCreatedAt());
    }
}
