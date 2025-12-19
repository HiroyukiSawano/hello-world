package org.example.myjweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 请求附件实体，对应 request_files 表。
 */
@Getter
@Setter
@TableName("request_files")
public class RequestFile {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 请求版本ID */
    @TableField("request_version_id")
    private Long requestVersionId;

    /** 表单字段名 */
    @TableField("field_name")
    private String fieldName;

    /** 文件名 */
    @TableField("file_name")
    private String fileName;

    /** 内容类型 */
    @TableField("content_type")
    private String contentType;

    /** 文件大小 */
    private Long size;

    /** 文件内容 */
    private byte[] content;

    /** 是否启用，0/1 */
    private Integer enabled;

    /** 排序号 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
