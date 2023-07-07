package com.polaris.lesscode.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.reflect.TypeToken;
import com.polaris.lesscode.app.bo.Event;
import com.polaris.lesscode.app.bo.ViewEvent;
import com.polaris.lesscode.app.config.RedisConfig;
import com.polaris.lesscode.app.consts.AppConsts;
import com.polaris.lesscode.app.consts.ThreadPools;
import com.polaris.lesscode.app.consts.CacheKeyConsts;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.entity.AppView;
import com.polaris.lesscode.app.mapper.AppMapper;
import com.polaris.lesscode.app.mapper.AppViewMapper;
import com.polaris.lesscode.app.req.AppViewAddReq;
import com.polaris.lesscode.app.req.AppViewEditReq;
import com.polaris.lesscode.app.req.AppViewSortReq;
import com.polaris.lesscode.app.resp.AppViewListResp;
import com.polaris.lesscode.app.resp.AppViewResp;
import com.polaris.lesscode.app.utils.PermissionUtil;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.permission.internal.enums.OperateAuthCode;
import com.polaris.lesscode.permission.internal.model.resp.AppAuthorityResp;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import com.polaris.lesscode.uc.internal.enums.PayLevel;
import com.polaris.lesscode.uc.internal.resp.GetOrgInfoResp;
import com.polaris.lesscode.util.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * App视图
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/22 14:57
 */
@Slf4j
@Service
public class AppViewServiceImpl extends ServiceImpl<AppViewMapper, AppView> implements AppViewService {

    @Autowired
    private AppViewMapper appViewMapper;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private PermissionUtil permissionUtil;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private GoPushService goPushService;

    @Autowired
    private UserCenterApi userCenterApi;

    @Override
    public AppViewListResp getAppViewListResp(Long orgId, Long operator, Long appId, Boolean authFilter, Boolean includePrivate) {
        AppViewListResp listResp = new AppViewListResp();
        // 无需过滤权限
        boolean allowEmpty = !Objects.equals(authFilter, true);
        Collection<Long> publicViewIds = new ArrayList<>();
        if (!allowEmpty) {
            permissionUtil.getUserAppViewList(orgId, appId, operator).forEach((k, v) -> {
                publicViewIds.add(k);
            });
        }
        List<AppView> appViewList = getAppViewList(orgId, appId, allowEmpty, publicViewIds, Objects.equals(includePrivate, true), operator);
        appViewList.forEach(e -> listResp.appendAppView(convToAppViewResp(e)));
        return listResp;
    }

    @Override
    public AppViewResp getAppViewResp(Long orgId, Long operator, Long appId, Long viewId) {
        // todo 是否要判断该视图是否为公有视图?
        AppView appView = getAssertExistAppView(orgId, appId, viewId);

        return convToAppViewResp(appView);
    }

    private static AppViewResp convToAppViewResp(AppView appView) {
        AppViewResp resp = new AppViewResp();
        resp.setId(appView.getId());
        resp.setOrgId(appView.getOrgId());
        resp.setAppId(appView.getAppId());
        resp.setType(appView.getType());
        resp.setViewName(appView.getViewName());
        resp.setRemark(appView.getRemark());
        if (Objects.nonNull(appView.getConfig())) {
            resp.setConfig(GsonUtils.readValue(appView.getConfig(), new TypeToken<Map<String, Object>>() {
            }.getType()));
        }
        resp.setIsPrivate(!Objects.equals(appView.getOwner(), 0L) && !Objects.equals(appView.getOwner(), -1L));
        resp.setIsLocked(Objects.equals(appView.getOwner(), -1L));
        resp.setOwner(appView.getOwner());
        resp.setSort(appView.getSort());
        return resp;
    }

    @Override
    public Long createAppView(Long orgId, Long operator, Long appId, AppViewAddReq appViewReq) {
        log.info("[新增App视图] -> orgId:{} operator:{} appId:{} appViewAddReq:{}", orgId, operator, appId, appViewReq);

        AppAuthorityResp appAuthorityResp = permissionService.appAuth(orgId, appId, operator);
        if (! Objects.equals(appViewReq.getIsPrivate(), true)) {
            if (! appAuthorityResp.hasAppRootPermission()
                    && ! appAuthorityResp.hasAppOptAuth(OperateAuthCode.PERMISSION_PRO_VIEW_MANAGEPUBLIC.getCode())
                    && ! appAuthorityResp.hasAppOptAuth(OperateAuthCode.HAS_CREATE_VIEW.getCode())) {
                throw new BusinessException(ResultCode.FORBIDDEN_ACCESS);
            }
        } else {
            if (! appAuthorityResp.hasAppRootPermission()
                    && ! appAuthorityResp.hasAppOptAuth(OperateAuthCode.PERMISSION_PRO_VIEW_MANAGEPRIVATE.getCode())
                    && ! appAuthorityResp.hasAppOptAuth(OperateAuthCode.HAS_CREATE_VIEW.getCode())) {
                throw new BusinessException(ResultCode.FORBIDDEN_ACCESS);
            }
        }

        Properties properties = new Properties();
        properties.put("appId", String.valueOf(appId));
        return redisConfig.synchronizedx(CacheKeyConsts.APP_VIEW_ADD_LOCK, properties, () -> {
            // view单个应用限制30个, 需求确认时间：2021-11-16,需求确认单位：产品和老板
            // 只有免费版才做应用视图数量限制，需求确认时间：2021-11-18，需求确认单位：产品
            GetOrgInfoResp getOrgInfoResp = userCenterApi.getOrgInfo(orgId).getData();
            if (Objects.equals(PayLevel.FREE.getCode(), getOrgInfoResp.getPayLevel())) {
                Integer count = appViewMapper.selectCount(new LambdaQueryWrapper<AppView>()
                        .eq(AppView::getAppId, appId)
                        .eq(AppView::getDelFlag, CommonConsts.FALSE));
                if (count >= 30) {
                    throw new BusinessException(ResultCode.APP_VIEW_LIMIT_ERROR);
                }
            }

            // 执行创建
            AppView appView = new AppView();

            // 设置appView配置
            setViewConfigParam(appView, appViewReq.getConfig());
//            // 公有视图所有者为0
//            appView.setOwner(Objects.equals(appViewReq.getIsPrivate(), true) ? operator : 0L);
            if (Objects.equals(appViewReq.getIsPrivate(), true)) {
                appView.setOwner(operator);
            }else {
                appView.setOwner(0L);
            }
            if (Objects.equals(appViewReq.getIsLocked(), true)) {
                appView.setOwner(-1L);
            }

            appView.setViewName(appViewReq.getViewName());
            appView.setRemark(appViewReq.getRemark());
            appView.setSort(System.currentTimeMillis());
            appView.setType(appViewReq.getType());

            appView.setOrgId(orgId);
            appView.setAppId(appId);
            appView.setCreator(operator);
            appView.setUpdator(operator);

            // 保存
            save(appView);

            // MQTT推送
            ThreadPools.POOLS.execute(() -> {
                App app = appMapper.get(appId);
                if (app == null) {
                    return;
                }
                ViewEvent viewEvent = new ViewEvent();
                viewEvent.setOrgId(orgId);
                viewEvent.setAppId(appId);
                viewEvent.setProjectId(app.getProjectId());
                viewEvent.setUserId(operator);
                viewEvent.setViewId(appView.getId());
                viewEvent.setNewData(convToAppViewResp(appView));
                Event event = new Event();
                event.setCategory(AppConsts.EventCategoryView);
                event.setType(AppConsts.EventTypeViewRefresh);
                event.setTimestamp(System.currentTimeMillis() * 1000000L);
                event.setPayload(viewEvent);
                try {
                    goPushService.pushMqtt(orgId, app.getProjectId(), event);
                } catch (JsonProcessingException e) {
                }
            });
            return appView.getId();
        });
    }

    private void setViewConfigParam(AppView appView, Map<String, Object> config) {
        if (Objects.nonNull(config)) {
            Object conditionObj = config.get(AppConsts.VIEW_CONFIG_CONDITION_PARAM_NAME);
            if (!(conditionObj instanceof Map)) {
               config.put(AppConsts.VIEW_CONFIG_CONDITION_PARAM_NAME, new HashMap<>());
            }
            appView.setConfig(GsonUtils.toJson(config));
        } else {
            appView.setConfig(AppConsts.EMPTY_MAP_JSON_STRING);
        }
    }

    @Override
    public Boolean editAppView(Long orgId, Long operator, Long appId, Long viewId, AppViewEditReq appViewReq) {
        log.info("[修改App视图] -> orgId:{}    operator:{}    appId:{}    viewId:{}    appViewEditReq:{}", orgId, operator, appId, viewId, appViewReq);
        AppView appView = getAssertExistAppView(orgId, appId, viewId);
        AppView oldAppView = new AppView();
        BeanUtils.copyProperties(appView, oldAppView);
        // 非法访问非公有视图
        if (!Objects.equals(appView.getOwner(), 0L) && (!Objects.equals(appView.getOwner(), -1L))) {
            if (!Objects.equals(appView.getOwner(), operator)) {
                log.warn("[修改App视图] -> 非法访问");
                throw new BusinessException(ResultCode.APP_VIEW_NOT_EXIST);
            }
        } else {
            AppAuthorityResp appAuthorityResp = permissionService.appAuth(orgId, appId, operator);
            if (! appAuthorityResp.hasAppRootPermission()
                    && ! appAuthorityResp.hasAppOptAuth(OperateAuthCode.PERMISSION_PRO_VIEW_MANAGEPUBLIC.getCode())) {
//                    && ! appAuthorityResp.hasViewUpdateAuth(viewId)){
                throw new BusinessException(ResultCode.FORBIDDEN_ACCESS);
            }
        }
        // 设置appView配置
        if (Objects.nonNull(appViewReq.getConfig())){
            setViewConfigParam(appView, appViewReq.getConfig());
        }
        if (StringUtils.isNotBlank(appViewReq.getViewName())){
            appView.setViewName(appViewReq.getViewName());
        }
        if (StringUtils.isNotBlank(appViewReq.getRemark())){
            appView.setRemark(appViewReq.getRemark());
        }
        if (Objects.nonNull(appViewReq.getType())){
            appView.setType(appViewReq.getType());
        }
        if (Objects.nonNull(appViewReq.getIsPrivate())){
            if (appViewReq.getIsPrivate()){
                appView.setOwner(operator);
            }else{
                appView.setOwner(0L);
            }
        }
        if (Objects.nonNull(appViewReq.getIsLocked())){
            if (appViewReq.getIsLocked()){
                appView.setOwner(-1L);
            }
        }
        appView.setUpdator(operator);
        appView.setUpdateTime(new Date());

        boolean suc = updateById(appView);
        if (suc) {
            ThreadPools.POOLS.execute(() -> {
                App app = appMapper.get(appId);
                if (app == null) {
                    return;
                }
                ViewEvent viewEvent = new ViewEvent();
                viewEvent.setOrgId(orgId);
                viewEvent.setAppId(appId);
                viewEvent.setProjectId(app.getProjectId());
                viewEvent.setUserId(operator);
                viewEvent.setViewId(appView.getId());
                viewEvent.setNewData(convToAppViewResp(appView));
                Event event = new Event();
                event.setCategory(AppConsts.EventCategoryView);
                event.setType(AppConsts.EventTypeViewRefresh);
                event.setTimestamp(System.currentTimeMillis() * 1000000L);
                event.setPayload(viewEvent);
                try {
                    goPushService.pushMqtt(orgId, app.getProjectId(), event);
                } catch (JsonProcessingException e) {
                }
            });
        }
        return suc;
    }

    @Override
    public Boolean deleteAppView(Long orgId, Long operator, Long appId, Long viewId) throws BusinessException {
        log.info("[删除App视图] -> orgId:{}    operator:{}    appId:{}    viewId:{}", orgId, operator, appId, viewId);
        AppView appView = getAssertExistAppView(orgId, appId, viewId);
        // 非法访问非公有视图
        if (!Objects.equals(appView.getOwner(), 0L) && (!Objects.equals(appView.getOwner(), -1L))) {
            if (!Objects.equals(appView.getOwner(), operator)) {
                log.warn("[修改App视图] -> 非法访问");
                throw new BusinessException(ResultCode.APP_VIEW_NOT_EXIST);
            }
        } else {
            AppAuthorityResp appAuthorityResp = permissionService.appAuth(orgId, appId, operator);
            if (! appAuthorityResp.hasAppRootPermission()
                    && ! appAuthorityResp.hasAppOptAuth(OperateAuthCode.PERMISSION_PRO_VIEW_MANAGEPUBLIC.getCode())
                    && ! appAuthorityResp.hasViewDeleteAuth(viewId)){
                throw new BusinessException(ResultCode.FORBIDDEN_ACCESS);
            }
        }
        // 删除
        boolean suc = removeById(viewId);
        if (suc){
            ThreadPools.POOLS.execute(() -> {
                App app = appMapper.get(appId);
                if (app == null) {
                    return;
                }
                ViewEvent viewEvent = new ViewEvent();
                viewEvent.setOrgId(orgId);
                viewEvent.setAppId(appId);
                viewEvent.setProjectId(app.getProjectId());
                viewEvent.setUserId(operator);
                viewEvent.setViewId(viewId);
                Event event = new Event();
                event.setCategory(AppConsts.EventCategoryView);
                event.setType(AppConsts.EventTypeViewDeleted);
                event.setTimestamp(System.currentTimeMillis() * 1000000L);
                event.setPayload(viewEvent);
                try {
                    goPushService.pushMqtt(orgId, app.getProjectId(), event);
                } catch (JsonProcessingException e) {
                }
            });
        }
        return suc;
    }

    /**
     * 获取App视图
     *
     * @param orgId
     * @param appId
     * @param viewId
     * @return {@code AppView}
     * @throws BusinessException {@link ResultCode#APP_VIEW_NOT_EXIST} App视图不存在或已删除
     */
    @Override
    public AppView getAssertExistAppView(Long orgId, Long appId, Long viewId) throws BusinessException {
        AppView appView = baseMapper.selectOne(
                new LambdaQueryWrapper<AppView>()
                        .eq(AppView::getOrgId, orgId)
                        .eq(AppView::getAppId, appId)
                        .eq(AppView::getId, viewId)
        );
        if (Objects.isNull(appView)) {
            throw new BusinessException(ResultCode.APP_VIEW_NOT_EXIST);
        }

        return appView;
    }

    @Override
    public List<AppView> getAppViewList(Long orgId, Long appId, boolean allowEmpty, Collection<Long> publicViewIds, boolean includePrivate, Long owner) {
        boolean idsIsEmpty = Objects.isNull(publicViewIds) || publicViewIds.isEmpty();
        if (!allowEmpty && idsIsEmpty && !includePrivate) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(
                Wrappers.lambdaQuery(new AppView())
                        .eq(AppView::getOrgId, orgId)
                        .eq(AppView::getAppId, appId)
                        .and(warp ->
                                // 公有的
                                warp.eq(allowEmpty && idsIsEmpty, AppView::getOwner, 0)
                                        // id包含
                                        .in(!idsIsEmpty, AppView::getId, publicViewIds)
                                        // 包含私有
                                        .or(includePrivate, warp1 -> warp1.eq(AppView::getOwner, owner)
                                                .or(warp2 -> warp2.eq(AppView::getOwner, -1)))
                        ).orderByAsc(AppView::getSort, AppView::getId)
        );
    }

    @Override
    public Boolean sortAppView(Long orgId, Long userId, Long appId, Long viewId, AppViewSortReq req) {
        AppView appView = getAssertExistAppView(orgId, appId, viewId);
        // 非法访问非公有视图
        if (!Objects.equals(appView.getOwner(), 0L) && (!Objects.equals(appView.getOwner(), -1L))) {
            if (!Objects.equals(appView.getOwner(), userId)) {
                log.warn("[修改App视图] -> 非法访问");
                throw new BusinessException(ResultCode.APP_VIEW_NOT_EXIST);
            }
        } else {
            AppAuthorityResp appAuthorityResp = permissionUtil.getAppAuth(orgId, appId, userId);
            if (! appAuthorityResp.hasViewUpdateAuth(viewId)){
                throw new BusinessException(ResultCode.FORBIDDEN_ACCESS);
            }
        }
        if (req.getBeforeId() != null && req.getBeforeId() > 0){
            AppView beforeView = getAssertExistAppView(orgId, appId, req.getBeforeId());
            baseMapper.update(null, new LambdaUpdateWrapper<AppView>()
                    .set(AppView::getSort, beforeView.getSort() + 1)
                    .set(AppView::getUpdator, userId)
                    .eq(AppView::getId, viewId));
            baseMapper.update(null, new LambdaUpdateWrapper<AppView>()
                    .setSql("sort = sort + 2")
                    .set(AppView::getUpdator, userId)
                    .ne(AppView::getId, beforeView.getId())
                    .ge(AppView::getSort, beforeView.getSort())
                    .eq(AppView::getAppId, appId));
        }else if (req.getAfterId() != null && req.getAfterId() > 0){
            AppView afterView = getAssertExistAppView(orgId, appId, req.getAfterId());
            baseMapper.update(null, new LambdaUpdateWrapper<AppView>()
                    .set(AppView::getSort, afterView.getSort() - 1)
                    .set(AppView::getUpdator, userId)
                    .eq(AppView::getId, viewId));
            baseMapper.update(null, new LambdaUpdateWrapper<AppView>()
                    .setSql("sort = sort - 2")
                    .set(AppView::getUpdator, userId)
                    .ne(AppView::getId, afterView.getId())
                    .le(AppView::getSort, afterView.getSort())
                    .eq(AppView::getAppId, appId));
        }
        return true;
    }
}
