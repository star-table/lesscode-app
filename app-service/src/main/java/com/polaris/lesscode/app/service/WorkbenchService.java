package com.polaris.lesscode.app.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polaris.lesscode.app.entity.Workbench;
import com.polaris.lesscode.app.enums.YesOrNo;
import com.polaris.lesscode.app.mapper.WorkbenchMapper;
import com.polaris.lesscode.app.req.WorkbenchAddReq;
import com.polaris.lesscode.app.req.WorkbenchUpdateReq;
import com.polaris.lesscode.app.resp.WorkbenchResp;
import com.polaris.lesscode.app.vo.ResultCode;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.exception.BusinessException;
import com.polaris.lesscode.util.ConvertUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Liu.B.J
 * @date: 2020/12/15 11:38
 * @description:
 */
@Service
public class WorkbenchService extends ServiceImpl<WorkbenchMapper, Workbench> {

    public List<WorkbenchResp> getWorkbench(Long userId, Long orgId){
        List<WorkbenchResp> resps = ConvertUtil.convertList(this.baseMapper.getList(userId, orgId), WorkbenchResp.class);
        if(resps == null){
            return new ArrayList<>();
        }
        return resps;
    }

    public WorkbenchResp addWorkbench(Long creator, Long orgId, WorkbenchAddReq req){
        Workbench wb = ConvertUtil.convert(req, Workbench.class);
        wb.setOrgId(orgId);
        wb.setCreator(creator);
        wb.setUpdator(creator);
        wb.setDelFlag(CommonConsts.NO_DELETE);
        boolean suc = this.baseMapper.insert(wb) > 0;
        if(! suc){
            throw new BusinessException(ResultCode.WORKBENCH_INSERT_FAIL);
        }
        return ConvertUtil.convert(wb, WorkbenchResp.class);
    }

    public WorkbenchResp updateWorkbench(Long userId, WorkbenchUpdateReq req){
        checkWorkbench(req.getId());
        Workbench wb = ConvertUtil.convert(req, Workbench.class);
        wb.setUpdator(userId);
        this.baseMapper.updateById(wb);
        return ConvertUtil.convert(wb, WorkbenchResp.class);
    }

    public boolean delWorkbench(Long userId, Long workbenchId){
        checkWorkbench(workbenchId);
        Workbench wb = new Workbench();
        wb.setId(workbenchId);
        wb.setUpdator(userId);
        wb.setDelFlag(YesOrNo.YES.getCode());
        boolean suc = this.baseMapper.updateById(wb) > 0;
        return suc;
    }

    private void checkWorkbench(Long id){
        Workbench wb = this.baseMapper.get(id);
        if(wb == null){
            throw new BusinessException(ResultCode.WORKBENCH_NOT_EXIST);
        }
    }

}
