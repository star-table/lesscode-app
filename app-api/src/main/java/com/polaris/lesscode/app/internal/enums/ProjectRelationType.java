package com.polaris.lesscode.app.internal.enums;

public enum ProjectRelationType {
    OWNER(1,"负责人"),
    PARTICIPANT(2, "参与人"),
    FOLLOWER(3, "关注人"),
    DEPT(25, "部门"),
    ;

    private Integer code;

    private String desc;

    private ProjectRelationType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ProjectRelationType formatOrNull(Integer code) {
        if (code == null) {
            return null;
        }
        ProjectRelationType[] enums = values();
        for (ProjectRelationType _enu : enums) {
            if (_enu.getCode().equals(code)) {
                return _enu;
            }
        }
        return null;
    }
}
