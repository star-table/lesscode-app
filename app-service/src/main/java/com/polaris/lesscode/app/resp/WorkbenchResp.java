package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @author: Liu.B.J
 * @date: 2020/12/15 11:55
 * @description:
 */
@Data
@ApiModel(value="工作台返回信息", description="工作台返回信息")
public class WorkbenchResp {

    private Long id;

    private Long orgId;

    private Long userId;

    private String name;

    private Integer sizeType;

    private String description;

    private String face;

    private Long creator;

    private Date createTime;

    private Long updator;

    private Date updateTime;

}
