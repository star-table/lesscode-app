package com.polaris.lesscode.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.BaseTest;
import com.polaris.lesscode.app.service.AppService;
import com.polaris.lesscode.form.internal.enums.FieldTypeEnums;
import com.polaris.lesscode.form.internal.req.AppFormExcelSaveReq;
import com.polaris.lesscode.form.internal.sula.Field;
import com.polaris.lesscode.vo.Result;

/**
 * @author wanglei
 * @version 1.0
 * @date 2020-08-04 2:50 下午
 */
public class AppTest extends BaseTest {

    @Autowired
    private AppService appService;

    @Test
    public void testExcelImport(){
        AppFormExcelSaveReq saveReq = new AppFormExcelSaveReq();
        saveReq.setGroupType(1);
        saveReq.setName("测试导入2");
        saveReq.setPkgId(1290167105928339458L);
        saveReq.setType(1);
        List<Map<String, Object>> config = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> cellConfig = new LinkedHashMap<>();
            cellConfig.put("type", 0);
            cellConfig.put("label", "测试"+i);
            cellConfig.put("col",i);
            config.add(cellConfig);
        }
        saveReq.setConfig(config);
        Result<?> result = appService.excelSave(saveReq,123L, 0L);
        assert result.isSuccess();
    }

    @Test
    public void testFieldTypeEnums(){
        Map<String, Object> map = new HashMap<>();
        map.put("type", 0);
        Field f = new Field();
        if (FieldTypeEnums.formatOrNull((Integer) map.get("type")) != null) {
            f.setType(FieldTypeEnums.formatOrNull((Integer) map.get("type")).getFormFieldType());
            f.setDataType(FieldTypeEnums.formatOrNull((Integer) map.get("type")).getType());
        }
        System.out.println("========Field======="+ JSON.toJSONString(f));
    }

}
