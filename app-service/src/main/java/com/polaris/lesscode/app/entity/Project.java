package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ppm_pro_project")
public class Project {
    private Long id;

    private Long orgId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    private String name;

    private Long owner;

    private Integer projectTypeId;

    private Integer publicStatus;

    private Integer templateFlag;

    private Long resourceId;

    @TableField(value = "is_filing")
    private Integer isFilling;

    private String remark;

    private Integer status;

    private Long creator;

    private Date createTime;

    private Long updator;

    private Date updateTime;

    private Integer isDelete;
}
