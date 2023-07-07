/**
 *
 */
package com.polaris.lesscode.app.internal.feign;

import com.polaris.lesscode.app.internal.api.AppViewApi;
import com.polaris.lesscode.app.internal.fallback.AppViewFallbackFactory;
import com.polaris.lesscode.consts.ApplicationConsts;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * AppView Feign Client
 * @author roamer
 * @version v1.0
 * @date 2021/1/26 10:52
 */
@FeignClient(contextId = "appViewProvider", value = ApplicationConsts.APPLICATION_APP, fallbackFactory = AppViewFallbackFactory.class)
public interface AppViewProvider extends AppViewApi {

}
