package com.polaris.lesscode.app;

import com.polaris.lesscode.BaseTest;
import com.polaris.lesscode.app.resp.AppPackageRelationResp;
import com.polaris.lesscode.app.service.AppPackageRelationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Liu.B.J
 * @Data: 2020/8/31 15:05
 * @Modified:
 */
public class AppPackageRelationTest extends BaseTest {

    @Autowired
    private AppPackageRelationService appPackageRelationService;

    @Test
    public void testGetList(){
        List<Long> pkgIds = new ArrayList<>();
        pkgIds.add(1290167105928339458L);
        pkgIds.add(1291689432805646338L);
        List<AppPackageRelationResp> resps = appPackageRelationService.getList(pkgIds,1L,11L);
        System.out.println("=====Test=====getList=====resps="+resps);
    }

}
