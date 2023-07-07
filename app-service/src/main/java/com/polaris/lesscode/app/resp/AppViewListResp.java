package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * App视图 列表响应模型
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/22 14:57
 */
@ApiModel("App视图 列表响应模型")
@Data
public class AppViewListResp implements Serializable {

    private static final long serialVersionUID = -9106711763719308719L;

    private static final List<AppViewResp> EMPTY_LIST = Collections.emptyList();

    @ApiModelProperty("视图列表")
    private List<AppViewResp> list;

//    @ApiModelProperty("是否具有新建视图的权限")
//    private Boolean hasCreate;

    @ApiModelProperty("总数")
    private long total;

    public AppViewListResp() {
        list = EMPTY_LIST;
//        hasCreate = Boolean.FALSE;
        total = 0L;
    }

    public void appendAppView(AppViewResp appView) {
        if (list == EMPTY_LIST || Objects.isNull(list)) {
            list = new ArrayList<>();
        }
        list.add(appView);
        if (total < 0) {
            total = 0L;
        }
        total++;
    }
}