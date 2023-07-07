package com.polaris.lesscode.app.resp;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author: Liu.B.J
 * @data: 2020/10/12 10:55
 * @modified:
 */
@Data
@ApiModel(value="应用包列表", description="应用包列表返回信息")
public class AppPackageListResp {

    private Boolean creatable;

    private List<AppPackageResp> appPkgList;

    private List<AppResp> appList;

}
