package org.example.myjweb.controller.dto;

import java.time.LocalDateTime;

/**
 * 请求资源接口模型。
 */
public final class ApiRequestDto {

    private ApiRequestDto() {
    }

    /**
     * 请求详情响应。
     */
    public record Response(Long id, LocalDateTime createdAt) {
    }
}
