/**
 * 
 */
package com.polaris.lesscode.app.internal.fallback;

import com.polaris.lesscode.app.internal.api.AppPackageApi;
import com.polaris.lesscode.app.internal.resp.AppPackageResp;
import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.feign.AbstractBaseFallback;
import com.polaris.lesscode.vo.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppPackageFallbackFactory extends AbstractBaseFallback implements FallbackFactory<AppPackageApi> {

	@Override
	public AppPackageApi create(Throwable cause) {
		
		return new AppPackageApi() {

			@Override
			public Result<List<AppPackageResp>> getAppPkgList(Long orgId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new ArrayList<AppPackageResp>());
				});
			}

			@Override
			public Result<AppPackageResp> getAppPkgInfo(Long pkgId) {
				return wrappDeal(ApplicationConsts.APPLICATION_APP,cause,()->{
					return Result.ok(new AppPackageResp());
				});
			}

		};
	}

}
