package org.example.myjweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 请求集合实体，对应 collections 表。
 */
@Getter
@Setter
@TableName("collections")
public class ApiCollection {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属项目ID */
    @TableField("project_id")
    private Long projectId;

    /** 集合名称 */
    private String name;

    /** 集合描述 */
    private String description;

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
