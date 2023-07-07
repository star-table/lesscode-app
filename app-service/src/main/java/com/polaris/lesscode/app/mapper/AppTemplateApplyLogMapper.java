package com.polaris.lesscode.app.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.app.entity.AppTemplateApplyLog;
import com.polaris.lesscode.app.entity.AppTemplateCate;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

public interface AppTemplateApplyLogMapper extends BaseMapper<AppTemplateApplyLog>{

    default void insertLog(AppTemplateApplyLog log){
        try{
            AppTemplateApplyLog appTemplateApplyLog = selectOne(new LambdaQueryWrapper<AppTemplateApplyLog>()
                    .eq(AppTemplateApplyLog::getCreator, log.getCreator())
                    .eq(AppTemplateApplyLog::getOrgId, log.getOrgId())
                    .eq(AppTemplateApplyLog::getTplId, log.getTplId()));
            if (appTemplateApplyLog == null){
                insert(log);
                return;
            }
            appTemplateApplyLog.setUpdateTime(new Date());
            updateById(appTemplateApplyLog);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
