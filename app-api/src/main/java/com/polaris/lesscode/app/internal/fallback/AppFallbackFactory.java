/**
 * 
 */
package com.polaris.lesscode.app.internal.fallback;

import com.polaris.lesscode.app.internal.api.AppApi;
import com.polaris.lesscode.app.internal.req.*;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.app.internal.resp.TaskResp;
import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.feign.AbstractBaseFallback;
import com.polaris.lesscode.vo.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

/**
 * @author Bomb.
 *
 */
@Component
public class AppFallbackFactory extends AbstractBaseFallback implements FallbackFactory<AppApi> {

	@Override
	public AppApi create(Throwable cause) {
		
		return new AppApi() {
			
			@Override
			public Result<List<AppResp>> getAppList(Long orgId, Long pkgId, Integer type) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new ArrayList<AppResp>());
				});
			}

			@Override
			public Result<AppResp> getAppInfo(Long orgId, Long appId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new AppResp());
				});
			}

			@Override
			public Result<List<AppResp>> getAppInfoList(Long orgId, Collection<Long> appIds) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new ArrayList<>());
				});
			}

			@Override
			public Result<TaskResp> startProcess(Long appId, String dataId, Long userId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new TaskResp());
				});
			}

			@Override
			public Result<AppResp> createApp(CreateAppReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new AppResp());
				});
			}

			@Override
			public Result<Boolean> updateApp(UpdateAppReq req) {
				return null;
			}

			@Override
			public Result<Boolean> deleteApp(DeleteAppReq req) {
				return null;
			}

			@Override
			public Result<AppResp> getAuthExtendsApp(Long appId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new AppResp());
				});
			}

			@Override
			public Result<Boolean> addProjectMember(Long appId, Integer relationType, Long relationId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(true);
				});
			}

			@Override
			public Result<Boolean> removeProjectMember(Long appId, Integer relationType, Long relationId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(true);
				});
			}

			@Override
			public Result<Boolean> isProjectMember(Long appId, Long orgId, Long userId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(false);
				});
			}

			@Override
			public Result<Map<Long, Boolean>> isProjectMemberBatch(IsProjectMemberBatchReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new HashMap<>());
				});
			}

			@Override
			public Result<Boolean> addAppMember(@RequestBody AddAppMemberInternalReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(false);
				});
			}

			@Override
			public Result<List<Long>> getTemplateIds(Long orgId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new ArrayList<>());
				});
			}

			@Override
			public Result<List<Long>> applyTemplate(Long orgId, Long userId, Long templateId, boolean isNewbieGuide) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new ArrayList<>());
				});
			}
		};
	}

}
