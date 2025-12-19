package org.example.myjweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 请求执行记录实体，对应 request_runs 表。
 */
@Getter
@Setter
@TableName("request_runs")
public class RequestRun {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 请求版本ID */
    @TableField("request_version_id")
    private Long requestVersionId;

    /** 环境ID */
    @TableField("environment_id")
    private Long environmentId;

    /** 响应状态码 */
    @TableField("status_code")
    private Integer statusCode;

    /** 耗时毫秒 */
    @TableField("duration_ms")
    private Integer durationMs;

    /** 是否成功，0/1 */
    private Integer success;

    /** 响应头 JSON */
    @TableField("response_headers_json")
    private String responseHeadersJson;

    /** 响应体 */
    @TableField("response_body")
    private byte[] responseBody;

    /** 响应体 MIME */
    @TableField("response_mime")
    private String responseMime;

    /** 响应字符集 */
    @TableField("response_charset")
    private String responseCharset;

    /** 响应体大小 */
    @TableField("response_size")
    private Long responseSize;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
