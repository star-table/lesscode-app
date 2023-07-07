package com.polaris.lesscode.app.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="项目返回信息", description="项目返回信息")
public class ProjectResp {
    private Long id;

    private Long orgId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    private String name;

    private Object owner;

    private Integer projectTypeId;

    private Integer publicStatus;

    private Integer templateFlag;

    private Long resourceId;

    private Integer isFilling;

    private String remark;

    private Integer status;

    private Long creator;

    private Date createTime;

    private Long updator;

    private Date updateTime;

    private Integer isDelete;
}
