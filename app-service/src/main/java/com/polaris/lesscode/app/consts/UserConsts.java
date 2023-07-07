package com.polaris.lesscode.app.consts;

import com.polaris.lesscode.uc.internal.resp.UserInfoResp;

public class UserConsts {

    public static final UserInfoResp SYSTEM_USER = new UserInfoResp();

    static {
        SYSTEM_USER.setId(0L);
        SYSTEM_USER.setName("小北");
        SYSTEM_USER.setType(1);
        SYSTEM_USER.setStatus(1);
        SYSTEM_USER.setIsDelete(2);
        SYSTEM_USER.setNamePy("XiaoBei");
    }
}
