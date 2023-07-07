package com.polaris.lesscode.app.internal.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.entity.AppWorkFlow;
import com.polaris.lesscode.app.mapper.AppWorkFlowMapper;
import com.polaris.lesscode.app.resp.AppWorkFlowResp;
import com.polaris.lesscode.util.ConvertUtil;
import org.springframework.stereotype.Service;

@Service
public class AppWorkFlowInternalService extends ServiceImpl<AppWorkFlowMapper, AppWorkFlow> {

    public AppWorkFlowResp getByAppId(Long appId){
        AppWorkFlow appWorkFlow = this.baseMapper.getByAppId(appId);
        return ConvertUtil.convert(appWorkFlow, AppWorkFlowResp.class);
    }

    public int insert(AppWorkFlow appWorkFlow){
        return this.baseMapper.insert(appWorkFlow);
    }

}
