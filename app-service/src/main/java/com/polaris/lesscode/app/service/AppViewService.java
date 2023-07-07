package com.polaris.lesscode.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.polaris.lesscode.app.entity.AppView;
import com.polaris.lesscode.app.req.AppViewAddReq;
import com.polaris.lesscode.app.req.AppViewEditReq;
import com.polaris.lesscode.app.req.AppViewSortReq;
import com.polaris.lesscode.app.resp.AppViewListResp;
import com.polaris.lesscode.app.resp.AppViewResp;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.exception.BusinessException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;

/**
 * App视图
 *
 * @author roamer
 * @version v1.0
 * @date 2021/1/22 14:57
 */
public interface AppViewService extends IService<AppView> {


    /**
     * 获取App视图列表
     *
     * @param orgId
     * @param operator
     * @param appId
     * @param authFilter     是否开启权限过滤
     * @param includePrivate 是否包含私有视图
     * @return {@code AppViewListResp} 视图列表数据
     */
    AppViewListResp getAppViewListResp(Long orgId, Long operator, Long appId, Boolean authFilter, Boolean includePrivate);


    /**
     * 获取App视图详情
     *
     * @param orgId
     * @param operator
     * @param appId
     * @param viewId
     * @return {@code AppViewResp} 视图详情
     */
    AppViewResp getAppViewResp(Long orgId, Long operator, Long appId, Long viewId);

    /**
     * 创建App视图
     *
     * @param orgId
     * @param operator
     * @param appId
     * @param appViewAddReq
     * @return {@code true} 创建成功
     */
    Long createAppView(Long orgId, Long operator, Long appId, AppViewAddReq appViewAddReq);

    /**
     * 修改App视图
     *
     * @param orgId
     * @param operator
     * @param appId
     * @param viewId
     * @param appViewEditReq
     * @return {@code true} 修改成功
     */
    Boolean editAppView(Long orgId, Long operator, Long appId, Long viewId, AppViewEditReq appViewEditReq);

    /**
     * 删除App视图
     *
     * @param orgId
     * @param operator
     * @param appId
     * @param viewId
     * @return {@code true} 删除成功
     */
    Boolean deleteAppView(Long orgId, Long operator, Long appId, Long viewId);


    /**
     * 获取App视图
     *
     * @param orgId
     * @param appId
     * @param viewId
     * @return {@code AppView}
     * @throws BusinessException {@link ResultCode#APP_VIEW_NOT_EXIST} App视图不存在或已删除
     */
    AppView getAssertExistAppView(Long orgId, Long appId, Long viewId) throws BusinessException;

    /**
     * 获取App视图列表
     *
     * @param orgId
     * @param appId
     * @param allowEmpty     publicViewIds是否可以为空
     * @param publicViewIds  指定的viewID列表
     * @param includePrivate 是否包含私有视图
     * @param owner          视图拥有者
     * @return {@code List<AppView>}
     */
    List<AppView> getAppViewList(Long orgId, Long appId, boolean allowEmpty, Collection<Long> publicViewIds, boolean includePrivate, Long owner);


    /**
     * 应用视图排序
     *
     * @param appId
     * @param viewId
     * @param req
     * @return
     */
    Boolean sortAppView(Long orgId, Long userId, Long appId, Long viewId, AppViewSortReq req);

}
