package org.example.myjweb.controller;

import java.util.List;

import org.example.myjweb.controller.dto.EnvironmentVariableDto;
import org.example.myjweb.entity.EnvironmentVariable;
import org.example.myjweb.service.EnvironmentVariableService;
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
 * 环境变量接口，管理环境下的变量列表。
 */
@RestController
@RequestMapping("/api")
public class EnvironmentVariableController {

    private final EnvironmentVariableService environmentVariableService;

    public EnvironmentVariableController(EnvironmentVariableService environmentVariableService) {
        this.environmentVariableService = environmentVariableService;
    }

    @PostMapping("/environments/{environmentId}/variables")
    public EnvironmentVariableDto.Response create(@PathVariable Long environmentId,
                                                  @RequestBody EnvironmentVariableDto.Request request) {
        EnvironmentVariable variable = new EnvironmentVariable();
        variable.setEnvironmentId(environmentId);
        variable.setName(request.name());
        variable.setValue(request.value());
        variable.setIsSecret(request.isSecret());
        variable.setIsEnabled(request.isEnabled());
        variable.setSortOrder(request.sortOrder());
        return toResponse(environmentVariableService.create(variable));
    }

    @GetMapping("/environments/{environmentId}/variables")
    public List<EnvironmentVariableDto.Response> listByEnvironment(@PathVariable Long environmentId) {
        return environmentVariableService.listByEnvironmentId(environmentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/environment-variables/{id}")
    public ResponseEntity<EnvironmentVariableDto.Response> find(@PathVariable Long id) {
        return environmentVariableService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PutMapping("/environment-variables/{id}")
    public ResponseEntity<EnvironmentVariableDto.Response> update(@PathVariable Long id,
                                                                  @RequestBody EnvironmentVariableDto.Request request) {
        EnvironmentVariable variable = new EnvironmentVariable();
        variable.setId(id);
        variable.setName(request.name());
        variable.setValue(request.value());
        variable.setIsSecret(request.isSecret());
        variable.setIsEnabled(request.isEnabled());
        variable.setSortOrder(request.sortOrder());
        boolean updated = environmentVariableService.update(variable);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return environmentVariableService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @DeleteMapping("/environment-variables/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = environmentVariableService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private EnvironmentVariableDto.Response toResponse(EnvironmentVariable variable) {
        return new EnvironmentVariableDto.Response(variable.getId(), variable.getEnvironmentId(), variable.getName(),
                variable.getValue(), variable.getIsSecret(), variable.getIsEnabled(), variable.getSortOrder(),
                variable.getCreatedAt(), variable.getUpdatedAt());
    }
}
