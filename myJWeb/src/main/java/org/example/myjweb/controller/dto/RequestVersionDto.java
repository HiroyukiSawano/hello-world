package org.example.myjweb.controller.dto;

import java.time.LocalDateTime;

/**
 * 请求版本接口模型。
 */
public final class RequestVersionDto {

    private RequestVersionDto() {
    }

    /**
     * 创建或更新请求版本的请求体。
     */
    public record Request(String name, Long folderId, String method, String url, String description, Integer timeoutMs,
                          Integer followRedirects, String bodyType, String headersJson, String queryParamsJson,
                          String cookiesJson, String authJson, String bodyJson, String formFieldsJson, byte[] bodyRaw,
                          String bodyMime, String graphqlQuery, String graphqlVariablesJson, String soapAction,
                          String note) {
    }

    /**
     * 请求版本详情响应。
     */
    public record Response(Long id, Long requestId, Integer version, String name, Long folderId, String method,
                           String url, String description, Integer timeoutMs, Integer followRedirects, String bodyType,
                           String headersJson, String queryParamsJson, String cookiesJson, String authJson,
                           String bodyJson, String formFieldsJson, String bodyMime, String graphqlQuery,
                           String graphqlVariablesJson, String soapAction, String note, LocalDateTime createdAt) {
    }
}
