package com.polaris.lesscode.app.bo;

import lombok.Data;

@Data
public class AppViewTemplate {

    private Long id;

    private String name;

    private Integer type;

    private String config;

    private Long owner;
}
