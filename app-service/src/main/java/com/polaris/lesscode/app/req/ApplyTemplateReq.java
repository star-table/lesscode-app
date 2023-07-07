package com.polaris.lesscode.app.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 应用模板请求结构体
 *
 * @Author Nico
 * @Date 2021/3/16 16:33
 **/
@Data
public class ApplyTemplateReq {

   @NotNull
   @ApiModelProperty("模板ID")
   private Long templateId;

   @ApiModelProperty("是否保留示例数据，1保留，2不保留")
   private Integer saveSampleFlag;

   @ApiModelProperty("父应用id")
   private Long parentId;

}
