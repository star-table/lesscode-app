/**
 * 
 */
package com.polaris.lesscode.app.internal.feign;

import com.polaris.lesscode.app.internal.api.AppActionApi;
import com.polaris.lesscode.app.internal.fallback.AppActionFallbackFactory;
import com.polaris.lesscode.consts.ApplicationConsts;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author admin
 *
 */
@FeignClient(value = ApplicationConsts.APPLICATION_APP, fallbackFactory =  AppActionFallbackFactory.class, contextId = "app")
public interface AppActionProvider extends AppActionApi {

}
