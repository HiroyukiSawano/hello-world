package org.example.myjweb.controller.dto;

import java.time.LocalDateTime;

/**
 * 环境接口模型。
 */
public final class EnvironmentDto {

    private EnvironmentDto() {
    }

    /**
     * 创建或更新环境的请求体。
     */
    public record Request(String name, Integer isDefault) {
    }

    /**
     * 环境详情响应。
     */
    public record Response(Long id, Long projectId, String name, Integer isDefault, LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
    }
}
