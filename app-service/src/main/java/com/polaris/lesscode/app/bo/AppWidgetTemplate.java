package com.polaris.lesscode.app.bo;

import lombok.Data;

@Data
public class AppWidgetTemplate {
    private Long id;
    private String name;
    private String description;
    private Long type;
    private String config;
}
