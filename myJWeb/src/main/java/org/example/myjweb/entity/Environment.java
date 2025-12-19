package org.example.myjweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 环境实体，对应 environments 表。
 */
@Getter
@Setter
@TableName("environments")
public class Environment {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 环境名称 */
    private String name;

    /** 是否默认环境，0/1 */
    @TableField("is_default")
    private Integer isDefault;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
