package com.polaris.lesscode.app.internal.feign;

import com.polaris.lesscode.app.internal.api.AppPackageApi;
import com.polaris.lesscode.app.internal.fallback.AppPackageFallbackFactory;
import com.polaris.lesscode.consts.ApplicationConsts;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = ApplicationConsts.APPLICATION_APP, fallbackFactory = AppPackageFallbackFactory.class, contextId = "appPackage")
public interface AppPackageProvider extends AppPackageApi {
	
}
