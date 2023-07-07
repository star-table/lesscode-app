package com.polaris.lesscode.app.internal.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.entity.AppAction;
import com.polaris.lesscode.app.internal.req.CreateAppActionReq;
import com.polaris.lesscode.app.mapper.AppActionMapper;
import com.polaris.lesscode.app.service.AppActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * app action service
 *
 * @Author Nico
 * @Date 2021/1/25 10:55
 **/
@Slf4j
@Service
public class AppActionInternalService extends ServiceImpl<AppActionMapper, AppAction> {

    @Autowired
    private AppActionService appActionService;

    public List<AppAction> createAction(Long orgId, Long operatorId, CreateAppActionReq req){
        return appActionService.createAction(orgId, operatorId, req);
    }

}



