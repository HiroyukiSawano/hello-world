package org.example.myjweb.controller;

import java.util.List;

import org.example.myjweb.controller.dto.EnvironmentDto;
import org.example.myjweb.entity.Environment;
import org.example.myjweb.service.EnvironmentService;
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
 * 环境接口，支持项目下环境管理与默认环境切换。
 */
@RestController
@RequestMapping("/api")
public class EnvironmentController {

    private final EnvironmentService environmentService;

    public EnvironmentController(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @PostMapping("/projects/{projectId}/environments")
    public EnvironmentDto.Response create(@PathVariable Long projectId, @RequestBody EnvironmentDto.Request request) {
        Environment environment = new Environment();
        environment.setProjectId(projectId);
        environment.setName(request.name());
        environment.setIsDefault(request.isDefault());
        return toResponse(environmentService.create(environment));
    }

    @GetMapping("/projects/{projectId}/environments")
    public List<EnvironmentDto.Response> listByProject(@PathVariable Long projectId) {
        return environmentService.listByProjectId(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/environments/{id}")
    public ResponseEntity<EnvironmentDto.Response> find(@PathVariable Long id) {
        return environmentService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PutMapping("/environments/{id}")
    public ResponseEntity<EnvironmentDto.Response> update(@PathVariable Long id,
                                                          @RequestBody EnvironmentDto.Request request) {
        Environment environment = new Environment();
        environment.setId(id);
        environment.setName(request.name());
        environment.setIsDefault(request.isDefault());
        boolean updated = environmentService.update(environment);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return environmentService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping("/environments/{id}/default")
    public ResponseEntity<Void> setDefault(@PathVariable Long id) {
        boolean updated = environmentService.setDefault(id);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/environments/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = environmentService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private EnvironmentDto.Response toResponse(Environment environment) {
        return new EnvironmentDto.Response(environment.getId(), environment.getProjectId(), environment.getName(),
                environment.getIsDefault(), environment.getCreatedAt(), environment.getUpdatedAt());
    }
}
