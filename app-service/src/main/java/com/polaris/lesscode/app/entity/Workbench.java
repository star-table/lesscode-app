package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: Liu.B.J
 * @date: 2020/12/15 11:08
 * @description:
 */
@Data
@TableName("lc_workbench")
public class Workbench {

    private Long id;

    private Long orgId;

    private Long userId;

    private String name;

    private String face;

    private int sizeType;

    private String description;

    private Long creator;

    private Date createTime;

    private Long updator;

    @TableField(update = "now()")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Version
    private Long version;

    private Integer delFlag;

}
