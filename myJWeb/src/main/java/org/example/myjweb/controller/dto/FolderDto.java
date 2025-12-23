package org.example.myjweb.controller.dto;

import java.time.LocalDateTime;

/**
 * 目录接口的请求与响应模型。
 */
public final class FolderDto {

    private FolderDto() {
    }

    /**
     * 创建或更新目录的请求体。
     */
    public record Request(String name, Long parentId, Integer sortOrder) {
    }

    /**
     * 目录详情响应。
     */
    public record Response(Long id, Long collectionId, Long parentId, String name, Integer sortOrder,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
    }
}
