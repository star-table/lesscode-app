package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * App视图
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/22 14:57
 */
@Data
@NoArgsConstructor
@TableName(value = "lc_app_view")
public class AppView {

    public static String CONFIG_CONDITION_PARAM_NAME = "condition";
    /**
     * ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId(value = "id")
    private Long id;

    /**
     * 组织ID
     */
    @TableField(value = "org_id")
    private Long orgId;

    /**
     * AppID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField(value = "app_id")
    private Long appId;

    /**
     * 视图类型
     */
    private Integer type;

    /**
     * 视图名称
     */
    @TableField(value = "view_name")
    private String viewName;

    /**
     * 视图配置
     */
    @TableField(value = "config")
    private String config;

    /**
     * 视图说明
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 所有者
     * 不为0则为用户专属
     */
    @TableField(value = "owner")
    private Long owner;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Long sort;

    /**
     * 版本号
     */
    @Version
    @TableField(value = "version")
    private Integer version;

    /**
     * 是否删除 1:删除 2:未删除
     */
    @TableLogic(value = "2", delval = "1")
    @TableField(value = "del_flag")
    private Byte delFlag;

    /**
     * 创建人
     */
    @TableField(value = "creator")
    private Long creator;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value = "updator")
    private Long updator;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}