package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.polaris.lesscode.app.enums.YesOrNo;
import lombok.Data;

import java.util.Date;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-03 11:27 上午
 */
@Data
@TableName("lc_app_pkg")
public class AppPackage {

    private Long id;

    private Long parentId;

    private Long orgId;

    private String name;

    private String icon;

    private Long creator;

    private Date createTime;

    private Long updator;

    @TableField(update = "now()")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Version
    private Long version;

    private Integer externalPkg = YesOrNo.NO.getCode();

    private String linkUrl;

    private Integer sort;

    private Integer delFlag;

    private String remark;

}
