package com.polaris.lesscode.app.internal.req;

import lombok.Data;

@Data
public class CreateViewReq {

    private Long orgId;

    private Long appId;

    private Long ownerId;

    private String name;

    private Long sort;

    private Integer type;

    private String config;

}
