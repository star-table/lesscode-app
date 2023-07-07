package com.polaris.lesscode.app.service;

import com.polaris.lesscode.gotable.internal.req.CreateSummeryTableRequest;
import com.polaris.lesscode.gotable.internal.req.CreateTableRequest;
import com.polaris.lesscode.gotable.internal.resp.CreateSummeryTableResp;
import com.polaris.lesscode.gotable.internal.resp.CreateTableResp;
import com.polaris.lesscode.gotable.internal.resp.TableSchemas;

import java.util.List;

public interface GoTableService {
    TableSchemas readSchema(Long tableId, Long orgId, Long userId);
    TableSchemas readSchemaByAppId(Long appId, Long orgId, Long tableId, Long userId);
    List<TableSchemas> readSchemas(List<Long> tableId, Long orgId, Long userId);
    CreateTableResp create(CreateTableRequest req, Long orgId, Long userId);
    CreateSummeryTableResp createSummery(CreateSummeryTableRequest req, Long orgId, Long userId);
}
