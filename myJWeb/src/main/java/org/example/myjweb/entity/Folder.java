package org.example.myjweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 目录实体，对应 folders 表。
 */
@Getter
@Setter
@TableName("folders")
public class Folder {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属集合ID */
    @TableField("collection_id")
    private Long collectionId;

    /** 父目录ID */
    @TableField("parent_id")
    private Long parentId;

    /** 目录名称 */
    private String name;

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
