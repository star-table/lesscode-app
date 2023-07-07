package com.polaris.lesscode.app.resp;

import com.polaris.lesscode.app.bo.Member;
import com.polaris.lesscode.app.consts.AppConsts;
import com.polaris.lesscode.permission.internal.model.resp.AppPerGroupListItem;
import com.polaris.lesscode.uc.internal.resp.MemberSimpleInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class AppMemberResp {

    private String id;

    private String name;

    private String avatar;

    private String type;

    private Integer status;

    private Integer isDelete;

    @ApiModelProperty("部门列表")
    private List<List<MemberSimpleInfo>> departments;

    @ApiModelProperty("角色列表")
    private List<MemberSimpleInfo> roles;

    @ApiModelProperty("是否为系统管理员")
    private boolean isSysAdmin;

    @ApiModelProperty("是否为子管理员")
    private boolean isSubAdmin;

    @ApiModelProperty("是否为所有者")
    private boolean isOwner;

    private List<AppPerGroupListItem> perGroups;

    public AppMemberResp() {
    }

    public AppMemberResp(Member member){
        this.id = member.getId().toString();
        this.name = member.getName();
        this.avatar = member.getAvatar();
        this.type = member.getType();
        this.status = member.getStatus();
        this.isDelete = member.getIsDelete();
        this.isSysAdmin = member.isSysAdmin();
        this.isSubAdmin = member.isSubAdmin();
        this.isOwner = member.isOwner();
        this.departments = member.getDepartments();
        this.roles = member.getRoles();
        this.perGroups = member.getPerGroups();
    }

    private int typeCode(){
        if (Objects.equals(AppConsts.MEMBER_USER_TYPE, type)){
            return 1;
        }else if (Objects.equals(AppConsts.MEMBER_DEPT_TYPE, type)){
            return 2;
        }else{
            return 3;
        }
    }

    private int score(){
        int score = 0;
        if (isSubAdmin || isSysAdmin){
            score ++;
        }
        if (isOwner){
            score ++;
        }
        return score;
    }

    public int compareTo(AppMemberResp r){
        if (typeCode() == r.typeCode()){
            if (score() > r.score()){
                return -1;
            }else if (score() == r.score()){
                Long id1 = Long.valueOf(id);
                Long id2 = Long.valueOf(r.id);
                if (id1 < id2){
                    return -1;
                }else{
                    return 1;
                }
            }else{
                return 1;
            }
        }else{
            if (typeCode() < r.typeCode()){
                return -1;
            }else if (typeCode() > r.typeCode()){
                return 1;
            }
        }
        return 0;
    }
}
