package com.polaris.lesscode.app.bo;

import com.polaris.lesscode.app.consts.AppConsts;
import com.polaris.lesscode.uc.internal.resp.DeptInfoResp;
import com.polaris.lesscode.uc.internal.resp.RoleInfoResp;
import com.polaris.lesscode.uc.internal.resp.UserInfoResp;
import com.polaris.lesscode.workflow.internal.bo.Member;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 成员解析器
 *
 * @author Nico
 * @date 2021/3/23 13:54
 */
@Data
public class Members {

    private Map<Long, UserInfoResp> users;

    private Map<Long, DeptInfoResp> depts;

    private Map<Long, RoleInfoResp> roles;

    public Members() {}

    public Members(Map<Long, UserInfoResp> users, Map<Long, DeptInfoResp> depts, Map<Long, RoleInfoResp> roles) {
        this.users = users;
        this.depts = depts;
        this.roles = roles;
    }

    public List<Member> getMembers(List<String> memberIds){
        if (CollectionUtils.isNotEmpty(memberIds)){
            List<Member> members = new ArrayList<>();
            for (String memberId: memberIds){
                Member member = null;
                if (memberId.startsWith(AppConsts.MEMBER_USER_TYPE)){
                    member = getUser(Long.valueOf(memberId.substring(AppConsts.MEMBER_USER_TYPE.length())));
                }else if (memberId.startsWith(AppConsts.MEMBER_DEPT_TYPE)){
                    member = getDept(Long.valueOf(memberId.substring(AppConsts.MEMBER_DEPT_TYPE.length())));
                }else if (memberId.startsWith(AppConsts.MEMBER_ROLE_TYPE)){
                    member = getRole(Long.valueOf(memberId.substring(AppConsts.MEMBER_ROLE_TYPE.length())));
                }
                if (member != null){
                    members.add(member);
                }
            }
            return members;
        }
        return null;
    }

    public List<Member> getUsers(List<Long> userIds){
       if (CollectionUtils.isNotEmpty(userIds)){
           List<Member> members = new ArrayList<>();
           for (Long userId: userIds){
               Member member = getUser(userId);
               if (member != null){
                   members.add(member);
               }
           }
           return members;
       }
       return null;
    }

    public List<Member> getDepts(List<Long> deptIds){
        if (CollectionUtils.isNotEmpty(deptIds)){
            List<Member> members = new ArrayList<>();
            for (Long deptId: deptIds){
                Member member = getDept(deptId);
                if (member != null){
                    members.add(member);
                }
            }
            return members;
        }
        return null;
    }

    public List<Member> getRoles(List<Long> roleIds){
        if (CollectionUtils.isNotEmpty(roleIds)){
            List<Member> members = new ArrayList<>();
            for (Long roleId: roleIds){
                Member member = getRole(roleId);
                if (member != null){
                    members.add(member);
                }
            }
            return members;
        }
        return null;
    }

    public Member getUser(Long userId){
        if (MapUtils.isNotEmpty(users)){
            UserInfoResp userInfoResp = users.get(userId);
            if (userInfoResp != null){
                return new Member(userInfoResp.getId(),
                        userInfoResp.getName(),
                        userInfoResp.getAvatar(),
                        AppConsts.MEMBER_USER_TYPE,
                        userInfoResp.getStatus(),
                        userInfoResp.getIsDelete());
            }
        }
        return null;
    }

    public Member getDept(Long deptId){
       if (MapUtils.isNotEmpty(depts)){
           DeptInfoResp deptInfoResp = depts.get(deptId);
           if (deptInfoResp != null){
               return new Member(deptInfoResp.getId(),
                       deptInfoResp.getName(),
                       StringUtils.EMPTY,
                       AppConsts.MEMBER_DEPT_TYPE,
                       deptInfoResp.getStatus(),
                       deptInfoResp.getIsDelete());
           }
       }
       return null;
   }

    public Member getRole(Long roleId){
       if (MapUtils.isNotEmpty(roles)){
           RoleInfoResp roleInfoResp = roles.get(roleId);
           if (roleInfoResp != null){
               return new Member(roleInfoResp.getId(),
                       roleInfoResp.getName(),
                       StringUtils.EMPTY,
                       AppConsts.MEMBER_ROLE_TYPE,
                       roleInfoResp.getStatus(),
                       roleInfoResp.getIsDelete());
           }
       }
       return null;
   }

}
