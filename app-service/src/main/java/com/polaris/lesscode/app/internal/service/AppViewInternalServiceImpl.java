package com.polaris.lesscode.app.internal.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.polaris.lesscode.app.entity.AppView;
import com.polaris.lesscode.app.internal.req.CreateViewReq;
import com.polaris.lesscode.app.internal.resp.AppViewResp;
import com.polaris.lesscode.app.service.AppViewService;
import com.polaris.lesscode.util.ConvertUtil;
import com.polaris.lesscode.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author roamer
 * @version v1.0
 * @date 2021/1/26 13:53
 */
@Slf4j
@Service
public class AppViewInternalServiceImpl implements AppViewInternalService {

    @Autowired
    private AppViewService appViewService;

    @Override
    public List<AppViewResp> getAppViewList(Long orgId, Long appId, Boolean includePublic, Long owner) {
        log.debug("[(内部调用) 查询App视图列表] -> orgId:{}    appId:{}   includePublic:{}    owner:{}", orgId, appId, includePublic, owner);
        List<AppView> appViewList = appViewService.getAppViewList(orgId, appId, Objects.equals(includePublic, true), null, !Objects.isNull(owner) && !Objects.equals(owner, 0L), owner);
        return appViewList.stream().map(AppViewInternalServiceImpl::convToAppViewResp).collect(Collectors.toList());
    }

    @Override
    public List<AppViewResp> createAppViews(List<CreateViewReq> reqs) {
        List<AppView> appViews = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reqs)){
            Long timestamp = System.currentTimeMillis();
            for (CreateViewReq req: reqs){
                AppView appView = new AppView();
                appView.setAppId(req.getAppId());
                appView.setOrgId(req.getOrgId());
                appView.setViewName(req.getName());
                appView.setConfig(req.getConfig());
                appView.setType(req.getType());
                appView.setSort(timestamp ++);
                appView.setOwner(req.getOwnerId());
                appViews.add(appView);
            }
            appViewService.saveBatch(appViews);
        }
        return ConvertUtil.convertList(appViews, AppViewResp.class);
    }

    private static AppViewResp convToAppViewResp(AppView appView) {
        AppViewResp resp = new AppViewResp();
        resp.setId(appView.getId());
        resp.setOrgId(appView.getOrgId());
        resp.setAppId(appView.getAppId());

        resp.setViewName(appView.getViewName());
        resp.setRemark(appView.getRemark());
        resp.setConfig(appView.getConfig());
        resp.setIsPrivate(!Objects.equals(appView.getOwner(), 0L));
        resp.setOwner(appView.getOwner());
        resp.setSort(appView.getSort());
        resp.setType(appView.getType());
        return resp;
    }
}
