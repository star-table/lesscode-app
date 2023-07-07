package com.polaris.lesscode.app.enums;

import java.util.HashSet;
import java.util.Set;

public enum TemplateResourceType {

    ITERATION("iteration", "保留项目中的迭代"),
    RECORDS("records", "保留项目中的任务"),

    ;

    private final String code;

    private final String desc;

    public static final Set<String> ALL_RESOURCES = new HashSet<>();

    static {
        for (TemplateResourceType type: values()){
            ALL_RESOURCES.add(type.getCode());
        }
    }

    TemplateResourceType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TemplateResourceType formatOrNull(String code) {
        if (null == code) {
            return null;
        }
        TemplateResourceType[] enums = values();
        for (TemplateResourceType _enu : enums) {
            if (_enu.getCode().equals(code)) {
                return _enu;
            }
        }
        return null;
    }

}
