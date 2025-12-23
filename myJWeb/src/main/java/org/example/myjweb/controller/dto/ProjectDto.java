package org.example.myjweb.controller.dto;

import java.time.LocalDateTime;

/**
 * 项目接口的请求与响应模型。
 */
public final class ProjectDto {

    private ProjectDto() {
    }

    /**
     * 创建或更新项目的请求体。
     */
    public record Request(String name, String description) {
    }

    /**
     * 项目详情响应。
     */
    public record Response(Long id, String name, String description, LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
    }
}
