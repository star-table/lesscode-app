package com.polaris.lesscode.app.service;

import com.polaris.lesscode.gotable.internal.api.GoTableApi;
import com.polaris.lesscode.gotable.internal.req.CreateSummeryTableRequest;
import com.polaris.lesscode.gotable.internal.req.CreateTableRequest;
import com.polaris.lesscode.gotable.internal.req.ReadTableSchemasByAppIdRequest;
import com.polaris.lesscode.gotable.internal.req.ReadTableSchemasRequest;
import com.polaris.lesscode.gotable.internal.resp.CreateSummeryTableResp;
import com.polaris.lesscode.gotable.internal.resp.CreateTableResp;
import com.polaris.lesscode.gotable.internal.resp.TableSchemas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoTableServiceImpl implements GoTableService{
    @Autowired
    private GoTableApi goTableApi;

    public TableSchemas readSchema(Long tableId, Long orgId, Long userId) {
        List<Long> tableIds = new ArrayList<>();
        tableIds.add(tableId);
        List<TableSchemas> list = readSchemas(tableIds, orgId,userId);
        return list.get(0);
    }

    // 理论上来说form只有汇总表的时候用到，一般都是通过tableId来获取
    public TableSchemas readSchemaByAppId(Long appId, Long orgId, Long tableId, Long userId) {
        if (tableId != null && !tableId.equals(0L)) {
            return readSchema(tableId,orgId,userId);
        }
        List<TableSchemas> list = goTableApi.readSchemasByAppId(new ReadTableSchemasByAppIdRequest(appId,true), orgId.toString(),userId.toString()).getTables();
        if (list != null && list.size() >= 1) {
            return list.get(0);
        }

        return null;
    }

    public List<TableSchemas> readSchemas(List<Long> tableIds, Long orgId, Long userId) {
        return goTableApi.readSchemas(new ReadTableSchemasRequest(tableIds, true, false), orgId.toString(),userId.toString()).getTables();
    }

    public CreateTableResp create(CreateTableRequest req, Long orgId, Long userId) {
        return goTableApi.create(req,orgId.toString(),userId.toString());
    }

    public  CreateSummeryTableResp createSummery(CreateSummeryTableRequest req, Long orgId, Long userId){
        return goTableApi.createSummery(req,orgId.toString(),userId.toString());
    }
}
