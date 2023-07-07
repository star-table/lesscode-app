package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: Liu.B.J
 * @Description:
 * @Data: 2020/8/31 10:20
 * @Modified:
 */
@Data
@TableName("lc_app_pkg_relation")
public class AppPackageRelation {

    private Long id;

    private Long orgId;

    private Long pkgId;

    private Long relationId;

    private Integer type;

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
