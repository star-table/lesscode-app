package com.polaris.lesscode.app.bo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @author cindy
 * date: 2020/6/8
 * time: 2:02 PM
 * description: jeecg-boot-parent
 */
@Data
public class EncodeDecodeBo {

    private JSONObject encodeData;

    private JSONObject decodeData;
}
