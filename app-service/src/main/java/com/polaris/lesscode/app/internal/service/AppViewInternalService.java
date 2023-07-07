package com.polaris.lesscode.app.internal.service;

import com.polaris.lesscode.app.internal.req.CreateViewReq;
import com.polaris.lesscode.app.internal.resp.AppViewResp;
import com.polaris.lesscode.vo.Result;

import java.util.List;

/**
 * App视图  （内部调用）
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/26 13:48
 */
public interface AppViewInternalService {

    List<AppViewResp> getAppViewList(Long orgId, Long appId, Boolean includePublic, Long owner);

    List<AppViewResp> createAppViews(List<CreateViewReq> reqs);
}
