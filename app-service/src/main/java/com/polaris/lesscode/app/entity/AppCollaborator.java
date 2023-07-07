package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName(value = "lc_app_collaborator")
public class AppCollaborator {

    public static String CONFIG_CONDITION_PARAM_NAME = "condition";
    /**
     * ID
     */
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
    @TableField(value = "app_id")
    private Long appId;

    /**
     * 数据id
     */
    private Long dataId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 成员类型（1部门2人员）
     */
    private Integer memberType;

    /**
     * 成员id
     */
    private Long memberId;

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