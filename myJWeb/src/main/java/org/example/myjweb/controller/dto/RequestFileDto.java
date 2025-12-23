package org.example.myjweb.controller.dto;

import java.time.LocalDateTime;

/**
 * 请求附件接口模型。
 */
public final class RequestFileDto {

    private RequestFileDto() {
    }

    /**
     * 创建附件的请求体。
     */
    public record Request(String fieldName, String fileName, String contentType, Long size, byte[] content,
                          Integer enabled, Integer sortOrder) {
    }

    /**
     * 附件详情响应。
     */
    public record Response(Long id, Long requestVersionId, String fieldName, String fileName, String contentType,
                           Long size, Integer enabled, Integer sortOrder, LocalDateTime createdAt) {
    }
}
