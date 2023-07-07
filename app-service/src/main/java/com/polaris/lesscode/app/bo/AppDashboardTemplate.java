package com.polaris.lesscode.app.bo;

import lombok.Data;

import java.util.List;

@Data
public class AppDashboardTemplate {

    private String config;

    private List<AppDashboardWidgetTemplate> dashboardWidgets;

}
