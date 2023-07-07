package com.polaris.lesscode.app.internal.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import com.polaris.lesscode.app.internal.api.AppVersionApi;
import com.polaris.lesscode.app.internal.fallback.AppVersionFallbackFactory;
import com.polaris.lesscode.consts.ApplicationConsts;

@FeignClient(value = ApplicationConsts.APPLICATION_APP, fallbackFactory = AppVersionFallbackFactory.class, contextId = "appVersion")
public interface AppVersionProvider extends AppVersionApi{
	
}
