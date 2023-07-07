/**
 *
 */
package com.polaris.lesscode.app.internal.fallback;

import com.polaris.lesscode.app.internal.api.AppViewApi;
import com.polaris.lesscode.app.internal.req.CreateViewReq;
import com.polaris.lesscode.app.internal.resp.AppViewResp;
import com.polaris.lesscode.consts.ApplicationConsts;
import com.polaris.lesscode.feign.AbstractBaseFallback;
import com.polaris.lesscode.vo.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * AppView Feign FallbackFactory
 * @author roamer
 * @version v1.0
 * @date 2021/1/26 10:52
 */
@Component
public class AppViewFallbackFactory extends AbstractBaseFallback implements FallbackFactory<AppViewApi> {

    @Override
    public AppViewApi create(Throwable throwable) {

        return new AppViewApi() {
            @Override
            public Result<List<AppViewResp>> getAppViewIdList(Long orgId, Long appId, Boolean includePublic, Long owner) {
                return wrappDeal(ApplicationConsts.APPLICATION_APP, throwable, () -> {
                    return Result.ok(Collections.emptyList());
                });
            }

            @Override
            public Result<List<AppViewResp>> createAppViews(List<CreateViewReq> reqs) {
                return wrappDeal(ApplicationConsts.APPLICATION_APP, throwable, () -> {
                    return Result.ok(Collections.emptyList());
                });
            }
        };
    }
}
