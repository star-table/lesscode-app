package com.polaris.lesscode.app.bo;

import com.polaris.lesscode.permission.internal.model.resp.AppPerGroupListItem;
import com.polaris.lesscode.uc.internal.resp.MemberSimpleInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 成员
 *
 * @author Nico
 * @date 2021/2/20 18:53
 */
@Data
public class Member {

    private Long id;

    private String name;

    private String avatar;

    private String type;

    private Integer status;

    private Integer isDelete;

    private List<List<MemberSimpleInfo>> departments;

    private List<MemberSimpleInfo> roles;

    @ApiModelProperty("是否为系统管理员")
    private boolean isSysAdmin;

    @ApiModelProperty("是否为子管理员")
    private boolean isSubAdmin;

    @ApiModelProperty("是否为所有者")
    private boolean isOwner;

    private List<AppPerGroupListItem> perGroups;

    public Member(Long id, String name, String avatar, String type, Integer status, Integer isDelete) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.status = status;
        this.isDelete = isDelete;
    }

    public Member(Long id, String name, String avatar, String type, Integer status, Integer isDelete, boolean isSysAdmin, boolean isSubAdmin, boolean isOwner) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.status = status;
        this.isDelete = isDelete;
        this.isSysAdmin = isSysAdmin;
        this.isSubAdmin = isSubAdmin;
        this.isOwner = isOwner;
    }

    public Member(Long id, String name, String avatar, String type, Integer status, Integer isDelete, boolean isSysAdmin, boolean isSubAdmin, boolean isOwner, List<AppPerGroupListItem> perGroups) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.status = status;
        this.isDelete = isDelete;
        this.isSysAdmin = isSysAdmin;
        this.isSubAdmin = isSubAdmin;
        this.isOwner = isOwner;
        this.perGroups = perGroups;
    }

    public Member() {
    }

}
