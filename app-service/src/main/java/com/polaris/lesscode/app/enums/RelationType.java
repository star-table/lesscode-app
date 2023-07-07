package com.polaris.lesscode.app.enums;

public enum RelationType {

    STAR(1, "星标");

    private final Integer type;
    private final String desc;

    RelationType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }


    public static RelationType formatOrNull(Integer type) {
        if (null == type) {
            return null;
        }
        RelationType[] enums = values();
        for (RelationType _enu : enums) {
            if (_enu.getType().equals(type)) {
                return _enu;
            }
        }
        return null;
    }

}
