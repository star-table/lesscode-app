/**
 * 
 */
package com.polaris.lesscode.app.internal.fallback;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.polaris.lesscode.app.internal.api.AppVersionApi;
import com.polaris.lesscode.app.internal.req.AppVersionAddReq;
import com.polaris.lesscode.app.internal.req.AppVersionUpdateReq;
import com.polaris.lesscode.app.internal.resp.AppVersionResp;
import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.feign.AbstractBaseFallback;
import com.polaris.lesscode.vo.Result;

import feign.hystrix.FallbackFactory;

/**
 * @author admin
 *
 */
@Component
public class AppVersionFallbackFactory extends AbstractBaseFallback implements FallbackFactory<AppVersionApi> {

	@Override
	public AppVersionApi create(Throwable cause) {
		return new AppVersionApi() {
			@Override
			public Result<List<AppVersionResp>> getAppVersionList(Long appId, Integer type, Integer status) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new ArrayList<AppVersionResp>());
				});
			}

			@Override
			public Result<AppVersionResp> addAppVersion(Long appId, AppVersionAddReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new AppVersionResp());
				});
			}

			@Override
			public Result<?> updateAppVersion(Long appId, Long appVersionId, AppVersionUpdateReq req) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok();
				});
			}
		};
	}

}
