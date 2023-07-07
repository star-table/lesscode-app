package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("移动app 数据响应模型")
public class MoveAppListResp {
    private List<MoveAppResp> moveAppRespList;
}
