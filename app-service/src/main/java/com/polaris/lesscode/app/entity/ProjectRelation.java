package com.polaris.lesscode.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ppm_pro_project_relation")
public class ProjectRelation {

    private Long id;

    private Long orgId;

    private Long projectId;

    private Long projectObjectTypeId;

    private Long teamId;

    private Long relationId;

    private Integer relationType;

    private Integer status;

    private Long creator;

    private Date createTime;

    private Long updator;

    private Date updateTime;

    private Integer version;

    private Integer isDelete;

    public ProjectRelation() {
    }

    public ProjectRelation(Long orgId, Long projectId, Long relationId, Integer relationType, Long creator, Long updator) {
        this.orgId = orgId;
        this.projectId = projectId;
        this.relationId = relationId;
        this.relationType = relationType;
        this.creator = creator;
        this.updator = updator;
    }
}
