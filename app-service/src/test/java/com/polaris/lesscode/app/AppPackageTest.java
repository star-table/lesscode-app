package com.polaris.lesscode.app;

import com.alibaba.fastjson.JSONObject;
import com.polaris.lesscode.BaseTest;
import com.polaris.lesscode.app.req.AppPackageAddReq;
import com.polaris.lesscode.app.req.AppPackageUpdateReq;
import com.polaris.lesscode.app.resp.AppPackageListResp;
import com.polaris.lesscode.app.resp.AppPackageResp;
import com.polaris.lesscode.app.service.AppPackageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-03 1:39 下午
 */
public class AppPackageTest extends BaseTest {

    @Autowired
    private AppPackageService appPackageService;

    @Test
    public void testSelectAppPackageList(){
        //List<AppPackageResp> appPackageResps = appPackageService.packageRespList(123L);
        //System.out.println("data:--->" + appPackageResps);
    }

    @Test
    public void testAppPackageDetail(){
        AppPackageResp appPackageResp = appPackageService.get(1290167105928339458L);
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("allowBlank", true);
        config.put("enable", true);
        config.put("visible", true);
        String string = JSONObject.toJSONString(config);

        System.out.println("--data:--->" + string);
    }

    @Test
    public void testAppPackageAdd(){
        AppPackageAddReq appPackageAddReq = new AppPackageAddReq();
        appPackageAddReq.setName("未命名应用");
        //appPackageAddReq.setOrgId(123L);
        appPackageService.addAppPackage(123L, 0L, appPackageAddReq);
    }

    @Test
    public void testPackageRespList(){
        AppPackageListResp list = appPackageService.packageRespList(1L,11L);
        System.out.println("=====Test=====testPackageRespList=====list="+list);

    }
}
