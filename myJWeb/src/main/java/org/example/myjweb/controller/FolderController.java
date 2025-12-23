package org.example.myjweb.controller;

import java.util.List;

import org.example.myjweb.controller.dto.FolderDto;
import org.example.myjweb.entity.Folder;
import org.example.myjweb.service.FolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 目录接口，管理集合中的分组。
 */
@RestController
@RequestMapping("/api")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/collections/{collectionId}/folders")
    public FolderDto.Response create(@PathVariable Long collectionId, @RequestBody FolderDto.Request request) {
        Folder folder = new Folder();
        folder.setCollectionId(collectionId);
        folder.setParentId(request.parentId());
        folder.setName(request.name());
        folder.setSortOrder(request.sortOrder());
        return toResponse(folderService.create(folder));
    }

    @GetMapping("/collections/{collectionId}/folders")
    public List<FolderDto.Response> listByCollection(@PathVariable Long collectionId,
                                                     @RequestParam(required = false) Long parentId) {
        return folderService.listByParent(collectionId, parentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/folders/{id}")
    public ResponseEntity<FolderDto.Response> find(@PathVariable Long id) {
        return folderService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PutMapping("/folders/{id}")
    public ResponseEntity<FolderDto.Response> update(@PathVariable Long id, @RequestBody FolderDto.Request request) {
        Folder folder = new Folder();
        folder.setId(id);
        folder.setName(request.name());
        folder.setParentId(request.parentId());
        folder.setSortOrder(request.sortOrder());
        boolean updated = folderService.update(folder);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return folderService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @DeleteMapping("/folders/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = folderService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private FolderDto.Response toResponse(Folder folder) {
        return new FolderDto.Response(folder.getId(), folder.getCollectionId(), folder.getParentId(), folder.getName(),
                folder.getSortOrder(), folder.getCreatedAt(), folder.getUpdatedAt());
    }
}
