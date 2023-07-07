package com.polaris.lesscode.app.resp;

import lombok.Data;

@Data
public class AppTemplateResourceResp {

    private String code;

    private String desc;

    public AppTemplateResourceResp(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public AppTemplateResourceResp() {
    }
}
