package com.polaris.lesscode.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.config.RedisConfig;
import com.polaris.lesscode.app.consts.CacheKeyConsts;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.entity.AppRelation;
import com.polaris.lesscode.app.entity.ProjectRelation;
import com.polaris.lesscode.app.internal.enums.AppRelationType;
import com.polaris.lesscode.app.mapper.AppMapper;
import com.polaris.lesscode.app.mapper.AppRelationMapper;
import com.polaris.lesscode.app.mapper.ProjectRelationMapper;
import com.polaris.lesscode.app.req.DelAppMemberReq;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.permission.internal.api.AppPermissionApi;
import com.polaris.lesscode.uc.internal.api.UserCenterApi;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


@Service
public class AppStarService extends ServiceImpl<AppRelationMapper, AppRelation> {

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private AppRelationMapper appRelationMapper;

    @Autowired
    private ProjectRelationMapper projectRelationMapper;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private UserCenterApi userCenterApi;

    @Autowired
    private AppPermissionApi appPermissionApi;

    public AppRelation addStar(Long orgId, Long userId, Long appId){
        App app = appMapper.get(orgId, appId);
        if (app == null){
            throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
        }
        AppRelation appRelation = new AppRelation(appId, orgId, AppRelationType.STAR.getCode(), userId, userId, userId);
        addRelation(appRelation);
        return appRelation;
    }

    public boolean delStar(Long orgId, Long userId, Long appId){
        App app = appMapper.get(orgId, appId);
        if (app == null){
            throw new BusinessException(ResultCode.APP_NOT_EXIST.getCode(), ResultCode.APP_NOT_EXIST.getMessage() + " org:" + orgId + " app:" + appId);
        }
        LambdaUpdateWrapper<AppRelation> updateWrapper = new LambdaUpdateWrapper<AppRelation>();
        updateWrapper.eq(AppRelation::getOrgId, orgId);
        updateWrapper.eq(AppRelation::getAppId, appId);
        updateWrapper.eq(AppRelation::getType, AppRelationType.STAR.getCode());
        updateWrapper.eq(AppRelation::getRelationId, userId);
        updateWrapper.set(AppRelation::getUpdator, userId);
        updateWrapper.set(AppRelation::getDelFlag, CommonConsts.TRUE);
        return update(updateWrapper);
    }

    public List<Long> getUserStarAppIds(Long orgId, Long userId){
        List<AppRelation> relations = list(new LambdaQueryWrapper<AppRelation>()
                .eq(AppRelation::getOrgId, orgId)
                .eq(AppRelation::getDelFlag, CommonConsts.FALSE)
                .eq(AppRelation::getType, AppRelationType.STAR.getCode())
                .eq(AppRelation::getRelationId, userId));
        List<Long> appIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(relations)){
            appIds = relations.stream().map(AppRelation::getAppId).collect(Collectors.toList());
        }
        return appIds;
    }

    public AppRelation getAppRelation(Long appId, Integer relationType, Long relationId){
        return appRelationMapper.selectOne(new LambdaQueryWrapper<AppRelation>()
                .eq(AppRelation::getDelFlag, CommonConsts.FALSE)
                .eq(AppRelation::getAppId, appId)
                .eq(AppRelation::getType, relationType)
                .eq(AppRelation::getRelationId, relationId)
                .last("limit 1"));
    }

    public void addRelation(AppRelation appRelation){
        if (getAppRelation(appRelation.getAppId(), appRelation.getType(), appRelation.getRelationId()) == null){
            Properties properties = new Properties();
            properties.setProperty("appId", String.valueOf(appRelation.getAppId()));
            redisConfig.synchronizedx(CacheKeyConsts.APP_RELATION_MEMBER_LOCK, properties, () -> {
                if (getAppRelation(appRelation.getAppId(), appRelation.getType(), appRelation.getRelationId()) == null){
                    save(appRelation);
                }
                return null;
            });
        }
    }

    public ProjectRelation getProjectRelation(Long projectId, Integer relationType, Long relationId){
        return projectRelationMapper.selectOne(new LambdaQueryWrapper<ProjectRelation>()
                .eq(ProjectRelation::getIsDelete, CommonConsts.FALSE)
                .eq(ProjectRelation::getProjectId, projectId)
                .eq(ProjectRelation::getRelationType, relationType)
                .eq(ProjectRelation::getRelationId, relationId)
                .last("limit 1"));
    }

    public void addRelation(ProjectRelation projectRelation){
        if (getAppRelation(projectRelation.getProjectId(), projectRelation.getRelationType(), projectRelation.getRelationId()) == null){
            Properties properties = new Properties();
            properties.setProperty("appId", String.valueOf(projectRelation.getProjectId()));
            redisConfig.synchronizedx(CacheKeyConsts.APP_RELATION_MEMBER_LOCK, properties, () -> {
                if (getAppRelation(projectRelation.getProjectId(), projectRelation.getRelationType(), projectRelation.getRelationId()) == null){
                    projectRelationMapper.insert(projectRelation);
                }
                return null;
            });
        }
    }

    public void deleteRelation(Long operatorId, ProjectRelation projectRelation){
        LambdaUpdateWrapper<ProjectRelation> updateWrapper = new LambdaUpdateWrapper<ProjectRelation>();
        updateWrapper.eq(ProjectRelation::getOrgId, projectRelation.getOrgId());
        updateWrapper.eq(ProjectRelation::getProjectId, projectRelation.getProjectId());
        updateWrapper.eq(ProjectRelation::getRelationType, projectRelation.getRelationType());
        updateWrapper.eq(ProjectRelation::getRelationId, projectRelation.getRelationId());
        updateWrapper.set(ProjectRelation::getUpdator, operatorId);
        updateWrapper.set(ProjectRelation::getIsDelete, CommonConsts.TRUE);
        projectRelationMapper.update(null, updateWrapper);
    }


}
