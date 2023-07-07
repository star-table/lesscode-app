package com.polaris.lesscode.app.bo;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class AppDashboardWidgetTemplate {
    private Long widgetId;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Boolean polling = false;
    private Integer frequency;
    private String config;
}
