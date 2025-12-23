package org.example.myjweb.controller;

import java.util.List;

import org.example.myjweb.controller.dto.ProjectDto;
import org.example.myjweb.entity.Project;
import org.example.myjweb.service.ProjectService;
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
 * 项目接口，提供基础 CRUD 能力。
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectDto.Response create(@RequestBody ProjectDto.Request request) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        return toResponse(projectService.create(project));
    }

    @GetMapping
    public List<ProjectDto.Response> list() {
        return projectService.listAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto.Response> find(@PathVariable Long id) {
        return projectService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto.Response> update(@PathVariable Long id, @RequestBody ProjectDto.Request request) {
        Project project = new Project();
        project.setId(id);
        project.setName(request.name());
        project.setDescription(request.description());
        boolean updated = projectService.update(project);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return projectService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = projectService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private ProjectDto.Response toResponse(Project project) {
        return new ProjectDto.Response(project.getId(), project.getName(), project.getDescription(),
                project.getCreatedAt(), project.getUpdatedAt());
    }
}
