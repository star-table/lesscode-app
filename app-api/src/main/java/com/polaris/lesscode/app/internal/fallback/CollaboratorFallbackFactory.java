package com.polaris.lesscode.app.internal.fallback;

import com.polaris.lesscode.app.internal.api.AppCollaboratorApi;
import com.polaris.lesscode.app.internal.req.UpdateCollaboratorReq;
import com.polaris.lesscode.feign.AbstractBaseFallback;
import com.polaris.lesscode.vo.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CollaboratorFallbackFactory extends AbstractBaseFallback implements FallbackFactory<AppCollaboratorApi> {

    @Override
    public AppCollaboratorApi create(Throwable throwable) {
        return new AppCollaboratorApi() {
//            @Override
//            public Result<List<Long>> getUserCollaboratorRoleIds(Long orgId, Long appId, Long tableId, Long userId) {
//                return Result.ok(new ArrayList<>());
//            }
        };
    }
}
