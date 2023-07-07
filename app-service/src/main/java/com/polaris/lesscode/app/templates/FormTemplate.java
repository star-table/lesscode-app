package com.polaris.lesscode.app.templates;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Liu.B.J
 */
@Data
public class FormTemplate implements Serializable {

    private String mode;

    public FormTemplate(){
        this.mode = "create";
    }

}
