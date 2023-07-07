/**
 * 
 */
package com.polaris.lesscode.app.internal.fallback;

import com.polaris.lesscode.app.internal.api.AppApi;
import com.polaris.lesscode.app.internal.api.AppStarApi;
import com.polaris.lesscode.app.internal.req.CreateAppReq;
import com.polaris.lesscode.app.internal.req.DeleteAppReq;
import com.polaris.lesscode.app.internal.req.UpdateAppReq;
import com.polaris.lesscode.app.internal.resp.AppResp;
import com.polaris.lesscode.app.internal.resp.TaskResp;
import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.feign.AbstractBaseFallback;
import com.polaris.lesscode.vo.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
public class AppStarFallbackFactory extends AbstractBaseFallback implements FallbackFactory<AppStarApi> {

	@Override
	public AppStarApi create(Throwable cause) {
		
		return new AppStarApi() {

			@Override
			public Result<Boolean> addStar(Long appId, Long orgId, Long userId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(true);
				});
			}

			@Override
			public Result<Boolean> deleteStar(Long appId, Long orgId, Long userId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(true);
				});
			}
		};
	}

}
