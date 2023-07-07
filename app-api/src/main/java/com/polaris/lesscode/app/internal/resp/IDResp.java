package com.polaris.lesscode.app.internal.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 只返回ID
 *
 * @author Nico
 * @date 2021/1/25 18:50
 */
@Data
public class IDResp {

    @ApiModelProperty("ID")
    private Long id;

    public IDResp() {
    }

    public IDResp(Long id) {
        this.id = id;
    }
}
