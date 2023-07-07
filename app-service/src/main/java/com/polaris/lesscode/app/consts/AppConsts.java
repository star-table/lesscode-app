package com.polaris.lesscode.app.consts;

/**
 * 常量
 *
 * @author roamer
 * @version v1.0
 * @date 2021/2/3 14:59
 */
public interface AppConsts {
    String EMPTY_MAP_JSON_STRING = "{}";
    String EMPTY_ARRAY_JSON_STRING = "[]";
    String VIEW_CONFIG_CONDITION_PARAM_NAME = "condition";

    String MEMBER_USER_TYPE = "U_";
    String MEMBER_ROLE_TYPE = "R_";
    String MEMBER_DEPT_TYPE = "D_";

    String MANAGE_GROUP_SYS = "ManageGroup.Sys";
    String MANAGE_GROUP_SUB = "ManageGroup.Sub";
    String MANAGE_GROUP_SUB_NORMALADMIN = "ManageGroup.Sub.NormalAdmin";

    int DAY7 = 1000 * 60 * 60 * 24 * 7;

    /**
     * 公共模板view权限用户
     */
    Long PUBLIC_TEMPLATE_USER_ID = 2L;
    Long PUBLIC_TEMPLATE_ORG_ID = 999L;

    Integer MEMBER_TYPE_DEPT = 1;
    Integer MEMBER_TYPE_USER = 2;
    Integer MEMBER_TYPE_ROLE = 3;

    Short PROJECT_TYPE_EMPTY = 48;

    String EventCategoryApp = "App";
    String EventCategoryView = "View";

    String EventTypeViewRefresh = "ViewRefresh";
    String EventTypeViewDeleted = "ViewDeleted";

    Integer EventTypeAppRefresh = 500; // "AppRefresh"
    Integer EventTypeAppDeleted = 501; // "AppDeleted";

}
