/**
 * 
 */
package com.polaris.lesscode.app.internal.feign;

import com.polaris.lesscode.app.internal.api.AppCollaboratorApi;
import com.polaris.lesscode.app.internal.api.AppStarApi;
import com.polaris.lesscode.app.internal.fallback.AppStarFallbackFactory;
import com.polaris.lesscode.app.internal.fallback.CollaboratorFallbackFactory;
import com.polaris.lesscode.consts.ApplicationConsts;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(value = ApplicationConsts.APPLICATION_APP, fallbackFactory =  CollaboratorFallbackFactory.class, contextId = "appCollaborator")
public interface AppCollaboratorProvider extends AppCollaboratorApi {

}
