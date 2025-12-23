package org.example.myjweb.controller.dto;

import java.time.LocalDateTime;

/**
 * 请求集合接口的请求与响应模型。
 */
public final class ApiCollectionDto {

    private ApiCollectionDto() {
    }

    /**
     * 创建或更新集合的请求体。
     */
    public record Request(String name, String description, Integer sortOrder) {
    }

    /**
     * 集合详情响应。
     */
    public record Response(Long id, Long projectId, String name, String description, Integer sortOrder,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
    }
}
