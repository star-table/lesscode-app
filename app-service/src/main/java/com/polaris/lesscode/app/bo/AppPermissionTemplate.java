package com.polaris.lesscode.app.bo;

import com.polaris.lesscode.dc.internal.dsl.Condition;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppPermissionTemplate {

    private String name;

    private String langCode;

    private String remark;

    private List<String> optAuth;

    private List<String> tableAuth;

    private Map<String, Map<String, Integer>> fieldAuth;

    private Condition dataAuth;

}
