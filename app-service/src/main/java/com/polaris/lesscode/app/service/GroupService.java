package com.polaris.lesscode.app.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.polaris.lesscode.app.bo.AfterMoveBo;
import com.polaris.lesscode.app.entity.App;
import com.polaris.lesscode.app.enums.ReferenceType;
import com.polaris.lesscode.app.mapper.AppMapper;
import com.polaris.lesscode.app.mapper.AppPackageMapper;
import com.polaris.lesscode.app.resp.AppResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Liu.B.J
 */
@Service
public class GroupService {

	@Autowired
	private AppPackageService appPackageService;

	@Autowired
	private AppService appService;

	@Autowired
	private AppPackageMapper appPackageMapper;

	@Autowired
	private AppMapper appMapper;

	/**
	 * 获取最大sort
	 * @param orgId
	 * @return
	 */
	public Long getMaxSort(Long orgId){
		App maxApp = appMapper.getMaxSort(orgId);
		long sort = 0;
		if(maxApp != null){
			sort = maxApp.getSort() == null ? 0 : maxApp.getSort();
		}
		return sort;
	}

	/**
	 * 获取最小sort
	 *
	 * @param orgId
	 * @return
	 */
	public Long getMinSort(Long orgId){
		App minApp = appMapper.getMinSort(orgId);
		long sort = 0;
		if(minApp != null){
			sort = minApp.getSort() == null ? 0 : minApp.getSort();
		}
		return sort;
	}

	/**
	 * 更新参照物排序
	 * 
	 * @param referenceId	参照物id
	 * @param referenceType	参照物类型
	 * @param updateSortType	更新排序的类型，1：before，2：after
	 * @return
	 */
	public AfterMoveBo updateReferenceSort(Long orgId, Long referenceId, Integer referenceType, int updateSortType) {
		if(updateSortType == ReferenceType.BEFORE.getType()) {
			return updateBeforeSort(orgId, referenceId, referenceType);
		}else {
			return updateAfterSort(orgId, referenceId, referenceType);
		}
	}

	/**
	 * 无参照物 待修改
	 * @param parentId
	 * @return
	 */
	public AfterMoveBo updateNoReferenceSort(Long parentId){
		// 默认从1开始
		return new AfterMoveBo(parentId, 1L);
	}
	
	/**
	 * 根据before参照物更新sort
	 * @param beforeId	before参照物id
	 * @param beforeType before参照物类型
	 */
	private AfterMoveBo updateBeforeSort(Long orgId, Long beforeId, Integer beforeType){
		AppResp before = appService.get(beforeId);
		moveBeforeApp(orgId, before.getParentId(), before.getSort());
		return new AfterMoveBo(before.getParentId(), before.getSort() + 1);
	}

	/**
	 * 根据after参照物更新sort
	 * @param afterId  after参照物od
	 * @param afterType  after参照物类型
	 * @return
	 */
	private AfterMoveBo updateAfterSort(Long orgId, Long afterId, Integer afterType){
		AppResp after = appService.get(afterId);
		moveAfterApp(orgId, after.getParentId(), after.getSort());
		return new AfterMoveBo(after.getParentId(), after.getSort() - 1);
	}

	private void moveBeforeApp(Long orgId, Long parentId, Long sort){
		LambdaUpdateWrapper<App> updateWrapper = new LambdaUpdateWrapper<App>()
				.setSql("sort = sort + 1")
				.eq(App :: getOrgId, orgId)
				.eq(App :: getParentId, parentId)
				.gt(App :: getSort, sort);
		appService.update(updateWrapper);
	}

	private void moveAfterApp(Long orgId, Long pkgId, Long sort){
		LambdaUpdateWrapper<App> updateWrapper = new LambdaUpdateWrapper<App>()
				.setSql("sort = sort - 1")
				.eq(App :: getOrgId, orgId)
				.eq(App :: getParentId, pkgId)
				.lt(App :: getSort, sort);
		appService.update(updateWrapper);
	}

}
