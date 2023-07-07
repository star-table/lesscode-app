package com.polaris.lesscode.app.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.internal.enums.AppType;
import com.polaris.lesscode.consts.CommonConsts;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface AppMapper extends BaseMapper<App>{

	default App get(Long orgId, Long appId) {
		return selectOne(new LambdaQueryWrapper<App>()
				.eq(App :: getId, appId)
				.eq(App :: getOrgId, orgId)
				.eq(App :: getDelFlag, CommonConsts.NO_DELETE)
				.last(" limit 1"));
	}

	default App get(Long orgId, Long appId, Integer appType) {
		return selectOne(new LambdaQueryWrapper<App>()
				.eq(App :: getId, appId)
				.eq(App :: getType, appType)
				.eq(App :: getOrgId, orgId)
				.eq(App :: getDelFlag, CommonConsts.NO_DELETE)
				.last(" limit 1"));
	}

	default App get(Long appId) {
		return selectOne(new LambdaQueryWrapper<App>()
				.eq(App :: getId, appId)
				.eq(App :: getDelFlag, CommonConsts.NO_DELETE)
				.last(" limit 1"));
	}

	default List<App> getListByIds(Long orgId, Collection<Long> ids){
		if(CollectionUtils.isEmpty(ids)){
			return new ArrayList<>();
		}
		return selectList(new LambdaQueryWrapper<App>()
				.in(App :: getId, ids)
				.eq(App :: getOrgId, orgId)
				.eq(App::getDelFlag, CommonConsts.NO_DELETE));
	}

	default List<App> getListByIds(Collection<Long> ids){
		if(CollectionUtils.isEmpty(ids)){
			return new ArrayList<>();
		}
		return selectList(new LambdaQueryWrapper<App>()
				.in(App :: getId, ids)
				.eq(App::getDelFlag, CommonConsts.NO_DELETE));
	}

	default List<App> getListByIdsWithoutProjects(Long orgId, Collection<Long> ids){
		if(CollectionUtils.isEmpty(ids)){
			return new ArrayList<>();
		}
		return selectList(new LambdaQueryWrapper<App>()
				.in(App :: getId, ids)
				.eq(App :: getOrgId, orgId)
				.ne(App :: getType, AppType.PROJECT.getCode())
				.eq(App::getDelFlag, CommonConsts.NO_DELETE));
	}

	default List<App> getByPkgId(Long pkgId) {
		return selectList(new LambdaQueryWrapper<App>()
				.eq(App :: getPkgId, pkgId)
				.eq(App :: getDelFlag, CommonConsts.NO_DELETE));
	}

	default List<App> getByPkgIds(List<Long> pkgIds) {
		if(CollectionUtils.isEmpty(pkgIds)){
			return new ArrayList<>();
		}
		return selectList(new LambdaQueryWrapper<App>()
				.in(App :: getPkgId, pkgIds)
				.eq(App :: getDelFlag, CommonConsts.NO_DELETE));
	}

	default App getMaxSort(Long orgId){
		LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<App>();
		queryWrapper.eq(App :: getDelFlag, CommonConsts.NO_DELETE)
				.eq(App :: getOrgId, orgId)
				.orderByDesc(App :: getSort)
				.last(" limit 1");
		return selectOne(queryWrapper);
	}

	default App getMinSort(Long orgId){
		LambdaQueryWrapper<App> queryWrapper = new LambdaQueryWrapper<App>();
		queryWrapper.eq(App :: getDelFlag, CommonConsts.NO_DELETE)
				.eq(App :: getOrgId, orgId)
				.orderByAsc(App :: getSort)
				.last(" limit 1");
		return selectOne(queryWrapper);
	}

	default List<App> getAppsByExtendsId(Long extendsId){
		return selectList(new LambdaQueryWrapper<App>()
				.eq(App :: getExtendsId, extendsId)
				.eq(App::getDelFlag, CommonConsts.NO_DELETE));
	}

	default Integer delAppsByExtendsId(Long extendsId, Long userId){
		return update(null, new LambdaUpdateWrapper<App>()
				.set(App :: getDelFlag, CommonConsts.DELETED)
				.set(App :: getUpdator, userId)
				.eq(App :: getExtendsId, extendsId));
	}

//	@Select("select name from lc_app p where id in (select app_id from lc_app_workflow where " +
//			"id in (select workflow_id from lc_app_process where instance_id in " +
//			"<foreach item='id' index='index' collection='procInstIds' open='(' separator=',' close=')'>" +
//			"#{id}" +
//			"</foreach>" +
//			"))")
//	public List<String> getAppNamesByProcInstIds(@Param("procInstIds") List<String> procInstIds);

	@Select("select name from lc_app p where id = (select app_id from lc_app_workflow where " +
			"id = (select workflow_id from lc_app_process where instance_id = #{procInstId}))")
	public String getAppNameByProcInstId(@Param("procInstId") String procInstId);

}
