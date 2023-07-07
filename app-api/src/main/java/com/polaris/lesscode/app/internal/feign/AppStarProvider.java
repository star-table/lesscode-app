/**
 * 
 */
package com.polaris.lesscode.app.internal.feign;

import com.polaris.lesscode.app.internal.api.AppApi;
import com.polaris.lesscode.app.internal.api.AppStarApi;
import com.polaris.lesscode.app.internal.fallback.AppFallbackFactory;
import com.polaris.lesscode.app.internal.fallback.AppStarFallbackFactory;
import com.polaris.lesscode.consts.ApplicationConsts;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(value = ApplicationConsts.APPLICATION_APP, fallbackFactory =  AppStarFallbackFactory.class, contextId = "appStar")
public interface AppStarProvider extends AppStarApi {

}
