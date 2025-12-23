package org.example.myjweb.controller;

import java.util.List;

import org.example.myjweb.controller.dto.ApiCollectionDto;
import org.example.myjweb.entity.ApiCollection;
import org.example.myjweb.service.ApiCollectionService;
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
 * 请求集合接口，负责项目下集合的 CRUD。
 */
@RestController
@RequestMapping("/api")
public class ApiCollectionController {

    private final ApiCollectionService apiCollectionService;

    public ApiCollectionController(ApiCollectionService apiCollectionService) {
        this.apiCollectionService = apiCollectionService;
    }

    @PostMapping("/projects/{projectId}/collections")
    public ApiCollectionDto.Response create(@PathVariable Long projectId, @RequestBody ApiCollectionDto.Request request) {
        ApiCollection collection = new ApiCollection();
        collection.setProjectId(projectId);
        collection.setName(request.name());
        collection.setDescription(request.description());
        collection.setSortOrder(request.sortOrder());
        return toResponse(apiCollectionService.create(collection));
    }

    @GetMapping("/projects/{projectId}/collections")
    public List<ApiCollectionDto.Response> listByProject(@PathVariable Long projectId) {
        return apiCollectionService.listByProjectId(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/collections/{id}")
    public ResponseEntity<ApiCollectionDto.Response> find(@PathVariable Long id) {
        return apiCollectionService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PutMapping("/collections/{id}")
    public ResponseEntity<ApiCollectionDto.Response> update(@PathVariable Long id,
                                                            @RequestBody ApiCollectionDto.Request request) {
        ApiCollection collection = new ApiCollection();
        collection.setId(id);
        collection.setName(request.name());
        collection.setDescription(request.description());
        collection.setSortOrder(request.sortOrder());
        boolean updated = apiCollectionService.update(collection);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return apiCollectionService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @DeleteMapping("/collections/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = apiCollectionService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private ApiCollectionDto.Response toResponse(ApiCollection collection) {
        return new ApiCollectionDto.Response(collection.getId(), collection.getProjectId(), collection.getName(),
                collection.getDescription(), collection.getSortOrder(), collection.getCreatedAt(),
                collection.getUpdatedAt());
    }
}
