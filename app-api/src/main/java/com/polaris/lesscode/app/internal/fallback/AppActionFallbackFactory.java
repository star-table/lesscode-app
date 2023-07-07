/**
 * 
 */
package com.polaris.lesscode.app.internal.fallback;

import com.polaris.lesscode.app.internal.api.AppActionApi;
import com.polaris.lesscode.app.internal.req.CreateAppActionReq;
import com.polaris.lesscode.feign.AbstractBaseFallback;
import com.polaris.lesscode.vo.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 应用动作降级实现
 *
 * @Author Nico
 * @Date 2021/1/26 20:36
 **/
@Component
public class AppActionFallbackFactory extends AbstractBaseFallback implements FallbackFactory<AppActionApi> {

	@Override
	public AppActionApi create(Throwable cause) {

		return new AppActionApi() {
			@Override
			public Result<Boolean> createAction(Long orgId, Long operatorId, CreateAppActionReq req) {
				return Result.ok(true);
			}
		};
	}

}
