package com.polaris.lesscode.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.polaris.lesscode.app.entity.AppTemplate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface AppTemplateMapper extends BaseMapper<AppTemplate>{

    @Update("update lc_app_template set hot = hot + 1 where id = #{templateId}")
    void addHot(@Param("templateId") long templateId);
}
