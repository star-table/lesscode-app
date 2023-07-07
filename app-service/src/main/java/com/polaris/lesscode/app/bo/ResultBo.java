package com.polaris.lesscode.app.bo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author cindy
 * date: 2020/6/8
 * time: 3:05 PM
 * description: jeecg-boot-parent
 */
@Data
public class ResultBo {

    private Boolean isSuc;

    private String msg;

    private EncodeDecodeBo result;
}
