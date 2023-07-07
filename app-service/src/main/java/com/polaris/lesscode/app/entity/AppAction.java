package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 应用Action
 *
 * @Author Nico
 * @Date 2021/1/21 20:35
 **/
@Data
@TableName("lc_app_action")
public class AppAction {

    /**
     * ID
     **/
    private Long id;

    /**
     * 组织id
     **/
    private Long orgId;

    /**
     * 对象id
     **/
    private Long objId;

    /**
     * 数据id
     **/
    private Long dataId;

    /**
     * 子表单数据id
     **/
    private Long subformDataId;

    /**
     * 子表单key
     **/
    private String subformKey;

    /**
     * 子表单名称
     **/
    private String subformName;

    /**
     * 对象类型
     **/
    private Integer objType;

    /**
     * 动作类型, 1: 创建，2: 修改，3：删除，4：评论
     **/
    private Integer action;

    /**
     * 修改前
     **/
    @TableField("`before`")
    private String before;

    /**
     * 修改后
     **/
    @TableField("`after`")
    private String after;

    /**
     * 修改内容
     **/
    private String changes;

    /**
     * 操作人
     **/
    private Long operator;

    /**
     * 操作时间
     **/
    private Date operateTime;

    private Long creator;

    private Date createTime;

    private Long updator;

    private Date updateTime;

    private Long version;

    private Integer delFlag;

}
