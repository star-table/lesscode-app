package com.polaris.lesscode.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.entity.AppPackageRelation;
import com.polaris.lesscode.app.enums.RelationType;
import com.polaris.lesscode.app.mapper.AppPackageRelationMapper;
import com.polaris.lesscode.app.req.AppPackageRelationAddReq;
import com.polaris.lesscode.app.req.AppPackageRelationUpdateReq;
import com.polaris.lesscode.app.resp.AppPackageRelationResp;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.util.ConvertUtil;
import com.polaris.lesscode.app.vo.ResultCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Liu.B.J
 * @Data: 2020/8/31 11:22
 * @Modified:
 */
@Service
public class AppPackageRelationService extends ServiceImpl<AppPackageRelationMapper, AppPackageRelation> {

    public List<AppPackageRelationResp> appPkgRelationRespList(Long orgId, Long userId) {
        List<AppPackageRelationResp> responseList = new ArrayList<>();
        List<AppPackageRelation> list = this.baseMapper.getList(orgId, userId);
        responseList = ConvertUtil.convertList(list, AppPackageRelationResp.class);
        return responseList;
    }

    public AppPackageRelationResp add(Long orgId, Long userId, AppPackageRelationAddReq req){
        if(RelationType.formatOrNull(req.getType()) == null){
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        AppPackageRelation appPkgRel = this.baseMapper.selectOne(new LambdaQueryWrapper<AppPackageRelation>()
                .eq(AppPackageRelation :: getOrgId, orgId)
                .eq(AppPackageRelation :: getPkgId, req.getPkgId())
                .eq(AppPackageRelation :: getType, req.getType())
                .eq(AppPackageRelation :: getRelationId, userId)
                .last(" limit 1"));
        if(appPkgRel == null){
            appPkgRel = new AppPackageRelation();
            appPkgRel.setOrgId(orgId);
            appPkgRel.setRelationId(userId);
            appPkgRel.setPkgId(req.getPkgId());
            appPkgRel.setType(req.getType());
            appPkgRel.setCreator(userId);
            appPkgRel.setUpdator(userId);
            appPkgRel.setDelFlag(CommonConsts.NO_DELETE);
            boolean suc = this.baseMapper.insert(appPkgRel) > 0;
            if(suc){
                return ConvertUtil.convert(appPkgRel, AppPackageRelationResp.class);
            }
        }else{
            appPkgRel.setUpdator(userId);
            appPkgRel.setDelFlag(CommonConsts.NO_DELETE);
            this.baseMapper.updateById(appPkgRel);
            return ConvertUtil.convert(appPkgRel, AppPackageRelationResp.class);
        }
        throw new BusinessException(ResultCode.APP_PACKAGE_RELATION_ADD_FAIL);
    }

    public void del(Long orgId, Long userId, AppPackageRelationUpdateReq req){
        if(RelationType.formatOrNull(req.getType()) == null){
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        boolean suc = this.baseMapper.update(null, new LambdaUpdateWrapper<AppPackageRelation>()
                .set(AppPackageRelation :: getDelFlag, CommonConsts.DELETED)
                .set(AppPackageRelation :: getUpdator, userId)
                .eq(AppPackageRelation :: getOrgId, orgId)
                .eq(AppPackageRelation :: getPkgId, req.getPkgId())
                .eq(AppPackageRelation :: getRelationId, userId)
                .eq(AppPackageRelation :: getType, req.getType())) > 0;
        if(! suc){
            throw new BusinessException(ResultCode.APP_PACKAGE_RELATION_ADD_FAIL);
        }
    }

    public List<AppPackageRelationResp> getList(List<Long> pkgIds, Long orgId, Long userId){
        if(CollectionUtils.isEmpty(pkgIds)){
            return new ArrayList<>();
        }
        List<AppPackageRelation> resps = this.baseMapper.selectList(new LambdaQueryWrapper<AppPackageRelation>()
                .eq(AppPackageRelation :: getOrgId, orgId)
                .inSql(AppPackageRelation :: getPkgId, StringUtils.join(pkgIds, ","))
                .eq(AppPackageRelation :: getRelationId, userId)
                .eq(AppPackageRelation :: getDelFlag, CommonConsts.NO_DELETE)
                .orderByDesc(AppPackageRelation :: getPkgId));
        return ConvertUtil.convertList(resps, AppPackageRelationResp.class);
    }

}
