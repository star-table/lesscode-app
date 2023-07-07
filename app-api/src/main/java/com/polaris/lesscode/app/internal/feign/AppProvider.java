/**
 * 
 */
package com.polaris.lesscode.app.internal.feign;

import org.springframework.cloud.openfeign.FeignClient;

import com.polaris.lesscode.app.internal.api.AppApi;
import com.polaris.lesscode.app.internal.fallback.AppFallbackFactory;
import com.polaris.lesscode.consts.ApplicationConsts;

/**
 * @author admin
 *
 */
@FeignClient(value = ApplicationConsts.APPLICATION_APP, fallbackFactory =  AppFallbackFactory.class, contextId = "app")
public interface AppProvider extends AppApi {

}
