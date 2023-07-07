package com.polaris.lesscode.app.bo;

import com.polaris.lesscode.permission.internal.model.resp.AppPermissionGroupResp;
import com.polaris.lesscode.project.internal.resp.ProjectTemplate;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 应用模板配置
 *
 * @author Nico
 * @date 2021/3/16 17:18
 */
@Data
public class AppTemplate {

    private Long id;

    private Long parentId;

    private Long mirrorViewId;

    private Long mirrorAppId;

    private Long extendsId;

    private String name;

    private Integer type;

    private String icon;

//    private FormJson formTemplate;

    private List<Map<String, Object>> dataTemplate;

//    private AppDashboardTemplate appDashboardTemplate;

    private ProjectTemplate appProjectTemplate;

    private List<AppViewTemplate> appViewTemplates;

    private List<AppWidgetTemplate> appWidgetTemplates;

    private List<AppPermissionGroupResp> appPermissionGroupsTemplates;

    boolean needData;
}
