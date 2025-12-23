package org.example.myjweb.controller.dto;

import java.time.LocalDateTime;

/**
 * 环境变量接口模型。
 */
public final class EnvironmentVariableDto {

    private EnvironmentVariableDto() {
    }

    /**
     * 创建或更新环境变量的请求体。
     */
    public record Request(String name, String value, Integer isSecret, Integer isEnabled, Integer sortOrder) {
    }

    /**
     * 环境变量详情响应。
     */
    public record Response(Long id, Long environmentId, String name, String value, Integer isSecret, Integer isEnabled,
                           Integer sortOrder, LocalDateTime createdAt, LocalDateTime updatedAt) {
    }
}
