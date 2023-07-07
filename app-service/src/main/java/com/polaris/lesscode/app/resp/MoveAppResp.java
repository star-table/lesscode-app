package com.polaris.lesscode.app.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("移动app 数据响应模型")
public class MoveAppResp {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long appId;

    private Long sort;

    private Integer type;

    private Long parentId;
}
