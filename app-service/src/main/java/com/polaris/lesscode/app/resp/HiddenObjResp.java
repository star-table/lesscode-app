/**
 * 
 */
package com.polaris.lesscode.app.resp;

import lombok.Data;

/**
 * @author admin
 *
 */
@Data
public class HiddenObjResp {

    /**
     * .对应前端页面id
     */
    private Integer hiddenId;
    
    /**
     *.前端页面控件需影藏
     */
    private Boolean hiddenFlg = true;
    
    /**
     * .备注
     */
    private String remark;
}
