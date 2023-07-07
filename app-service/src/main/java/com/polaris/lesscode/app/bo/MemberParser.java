package com.polaris.lesscode.app.bo;

import com.alibaba.fastjson.JSON;
import com.polaris.lesscode.app.consts.AppConsts;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员解析器
 *
 * @author Nico
 * @date 2021/3/23 13:54
 */
@Data
public class MemberParser {

    private List<Long> userIds;

    private List<Long> roleIds;

    private List<Long> deptIds;

    private boolean hasDepartmentOwner;

    private List<String> memberIds;

    public MemberParser(){
        this.userIds = new ArrayList<>();
        this.roleIds = new ArrayList<>();
        this.deptIds = new ArrayList<>();
    }

    public static MemberParser parse(List<String> memberIds){
        if (CollectionUtils.isEmpty(memberIds)){
            return null;
        }
        MemberParser parser = new MemberParser();
        for (String memberId: memberIds){
            if (memberId.startsWith(AppConsts.MEMBER_USER_TYPE)){
                parser.userIds.add(Long.valueOf(memberId.substring(AppConsts.MEMBER_USER_TYPE.length())));
            }else if (memberId.startsWith(AppConsts.MEMBER_DEPT_TYPE)){
                parser.deptIds.add(Long.valueOf(memberId.substring(AppConsts.MEMBER_DEPT_TYPE.length())));
            }else if (memberId.startsWith(AppConsts.MEMBER_ROLE_TYPE)){
                parser.roleIds.add(Long.valueOf(memberId.substring(AppConsts.MEMBER_ROLE_TYPE.length())));
            }
        }
        parser.memberIds = memberIds;
        return parser;
    }

    public static MemberParser parse(String memberIdsJson){
        if (StringUtils.isNotBlank(memberIdsJson)){
            return parse(JSON.parseArray(memberIdsJson, String.class));
        }
        return null;
    }

    public int memberSize(){
        return userIds.size() + deptIds.size() + roleIds.size();
    }
}
