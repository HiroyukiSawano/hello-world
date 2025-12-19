package org.example.myjweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 请求版本实体，对应 request_versions 表。
 */
@Getter
@Setter
@TableName("request_versions")
public class RequestVersion {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 请求ID */
    @TableField("request_id")
    private Long requestId;

    /** 版本号 */
    private Integer version;

    /** 请求名称 */
    private String name;

    /** 所属目录ID */
    @TableField("folder_id")
    private Long folderId;

    /** HTTP 方法 */
    private String method;

    /** 请求地址 */
    private String url;

    /** 请求描述 */
    private String description;

    /** 超时毫秒 */
    @TableField("timeout_ms")
    private Integer timeoutMs;

    /** 是否跟随重定向，0/1 */
    @TableField("follow_redirects")
    private Integer followRedirects;

    /** 请求体类型 */
    @TableField("body_type")
    private String bodyType;

    /** 请求头 JSON */
    @TableField("headers_json")
    private String headersJson;

    /** 查询参数 JSON */
    @TableField("query_params_json")
    private String queryParamsJson;

    /** Cookie JSON */
    @TableField("cookies_json")
    private String cookiesJson;

    /** 认证 JSON */
    @TableField("auth_json")
    private String authJson;

    /** JSON 请求体 */
    @TableField("body_json")
    private String bodyJson;

    /** 表单字段 JSON */
    @TableField("form_fields_json")
    private String formFieldsJson;

    /** 原始请求体 */
    @TableField("body_raw")
    private byte[] bodyRaw;

    /** 请求体 MIME */
    @TableField("body_mime")
    private String bodyMime;

    /** GraphQL 查询 */
    @TableField("graphql_query")
    private String graphqlQuery;

    /** GraphQL 变量 JSON */
    @TableField("graphql_variables_json")
    private String graphqlVariablesJson;

    /** SOAPAction */
    @TableField("soap_action")
    private String soapAction;

    /** 备注 */
    private String note;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
