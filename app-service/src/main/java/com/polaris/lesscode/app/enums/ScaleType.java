package com.polaris.lesscode.app.enums;

/**
 * @author: Liu.B.J
 * @date: 2020/12/15 11:46
 * @description:
 */
public enum ScaleType {

    SMALL(1, "0～19人"),

    MEDIUM(2, "2~49人"),

    LARGE(3, "50人以上");

    private Integer code;

    private String desc;

    private ScaleType(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
