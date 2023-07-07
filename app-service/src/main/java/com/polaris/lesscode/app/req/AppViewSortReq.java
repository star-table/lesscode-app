package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;


@ApiModel("App视图 排序接口")
@Data
public class AppViewSortReq implements Serializable {

    private static final long serialVersionUID = -9106711763719308719L;

    @ApiModelProperty("前面的id")
    private Long beforeId;

    @ApiModelProperty("后边的id")
    private Long afterId;

}