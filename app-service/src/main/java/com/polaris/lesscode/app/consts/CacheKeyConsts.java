package com.polaris.lesscode.app.consts;

import com.polaris.lesscode.consts.CommonConsts;

public interface CacheKeyConsts {

	String APP_VERSION_ADD_LOCK = CommonConsts.LOCK_ROOT_KEY + "appversion:add:${appId}:${type}";

	String APP_RELATION_MEMBER_LOCK = CommonConsts.LOCK_ROOT_KEY + "app:${appId}:relation:member";

	String PROJECT_RELATION_CACHE_KEY = "polaris:projectsvc:org_${orgId}:project_${projectId}:baseinfo";

	String APP_VIEW_ADD_LOCK = CommonConsts.LOCK_ROOT_KEY + "app:${appId}:create_view";
}
