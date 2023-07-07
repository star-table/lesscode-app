package com.polaris.lesscode.app.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.bo.*;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.entity.AppTemplate;
import com.polaris.lesscode.app.entity.AppTemplateCate;
import com.polaris.lesscode.app.entity.AppView;
import com.polaris.lesscode.app.enums.AppTemplateType;
import com.polaris.lesscode.app.internal.enums.AppType;
import com.polaris.lesscode.app.mapper.*;
import com.polaris.lesscode.app.req.ApplyTemplateReq;
import com.polaris.lesscode.app.req.CreateTemplateReq;
import com.polaris.lesscode.app.resp.AppTemplateCateResp;
import com.polaris.lesscode.app.resp.AppTemplateResp;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.dashboard.internal.req.DashboardAppTemplateReq;
import com.polaris.lesscode.dashboard.internal.req.DashboardTemplate;
import com.polaris.lesscode.dashboard.internal.resp.DashboardResp;
import com.polaris.lesscode.dashboard.internal.resp.DashboardWidgetResp;
import com.polaris.lesscode.dashboard.internal.resp.WidgetResp;
import com.polaris.lesscode.dc.internal.api.DataCenterApi;
import com.polaris.lesscode.dc.internal.dsl.*;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.form.internal.api.AppFormApi;
import com.polaris.lesscode.form.internal.feign.AppFormProvider;
import com.polaris.lesscode.form.internal.resp.AppFormResp;
import com.polaris.lesscode.form.internal.sula.FormJson;
import com.polaris.lesscode.permission.internal.api.PermissionApi;
import com.polaris.lesscode.permission.internal.enums.DefaultAppPermissionGroupType;
import com.polaris.lesscode.permission.internal.feign.AppPermissionProvider;
import com.polaris.lesscode.permission.internal.model.UserPermissionVO;
import com.polaris.lesscode.permission.internal.model.req.InitAppPermissionReq;
import com.polaris.lesscode.project.internal.api.ProjectApi;
import com.polaris.lesscode.project.internal.resp.ApplyProjectTemplateResp;
import com.polaris.lesscode.project.internal.resp.ProjectTemplate;
import com.polaris.lesscode.util.*;
import edp.davinci.internal.api.DashboardApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppTemplateCateService extends ServiceImpl<AppTemplateCateMapper, AppTemplateCate> {

    @Autowired
    private AppTemplateCateMapper appTemplateCateMapper;

    public List<AppTemplateCateResp> getCateList(){
        List<AppTemplateCate> appTemplateCates = appTemplateCateMapper.selectList(new LambdaQueryWrapper<AppTemplateCate>()
                .eq(AppTemplateCate::getDelFlag, CommonConsts.FALSE)
                .eq(AppTemplateCate::getType, 1)
                .orderByAsc(AppTemplateCate::getSortId));
        return ConvertUtil.convertList(appTemplateCates, AppTemplateCateResp.class);
    }

}



