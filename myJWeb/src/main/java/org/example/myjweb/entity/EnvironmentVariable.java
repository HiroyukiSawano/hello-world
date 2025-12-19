package org.example.myjweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 环境变量实体，对应 environment_variables 表。
 */
@Getter
@Setter
@TableName("environment_variables")
public class EnvironmentVariable {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属环境ID */
    @TableField("environment_id")
    private Long environmentId;

    /** 变量名称 */
    private String name;

    /** 变量值 */
    private String value;

    /** 是否敏感，0/1 */
    @TableField("is_secret")
    private Integer isSecret;

    /** 是否启用，0/1 */
    @TableField("is_enabled")
    private Integer isEnabled;

    /** 排序号 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
