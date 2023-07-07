package com.polaris.lesscode.app.consts;

import com.polaris.lesscode.permission.internal.enums.AppPerDefaultGroupLangCode;
import com.polaris.lesscode.permission.internal.model.resp.AppPerGroupListItem;

public class PermissionConsts {

    public final static String TPL_DELETE = "Permission.Org.Project.Create";  //删除模板
    public final static String TPL_SAVE_AS = "Permission.Org.Project.Create";  //另存为模板

    public static AppPerGroupListItem ADMIN_PER_GROUP = new AppPerGroupListItem();
    public static AppPerGroupListItem EDIT_PER_GROUP = new AppPerGroupListItem();
    public static AppPerGroupListItem READ_PER_GROUP = new AppPerGroupListItem();
    public static AppPerGroupListItem OWNER_PER_GROUP = new AppPerGroupListItem();
    public static AppPerGroupListItem PROJECT_MEMBER_PER_GROUP = new AppPerGroupListItem();

    static {
        // 管理员
        ADMIN_PER_GROUP.setId(1L);
        ADMIN_PER_GROUP.setLangCode(AppPerDefaultGroupLangCode.FORM_ADMINISTRATOR.getCode());
        ADMIN_PER_GROUP.setName(AppPerDefaultGroupLangCode.FORM_ADMINISTRATOR.getName());
        ADMIN_PER_GROUP.setRemake(AppPerDefaultGroupLangCode.FORM_ADMINISTRATOR.getDesc());
        ADMIN_PER_GROUP.setReadOnly(1);

        // 编辑
        EDIT_PER_GROUP.setId(2L);
        EDIT_PER_GROUP.setLangCode(AppPerDefaultGroupLangCode.EDIT.getCode());
        EDIT_PER_GROUP.setName(AppPerDefaultGroupLangCode.EDIT.getName());
        EDIT_PER_GROUP.setRemake(AppPerDefaultGroupLangCode.EDIT.getDesc());
        EDIT_PER_GROUP.setReadOnly(1);

        // 查看
        READ_PER_GROUP.setId(3L);
        READ_PER_GROUP.setLangCode(AppPerDefaultGroupLangCode.READ.getCode());
        READ_PER_GROUP.setName(AppPerDefaultGroupLangCode.READ.getName());
        READ_PER_GROUP.setRemake(AppPerDefaultGroupLangCode.READ.getDesc());
        READ_PER_GROUP.setReadOnly(1);


        // 负责人
        OWNER_PER_GROUP.setId(4L);
        OWNER_PER_GROUP.setLangCode(AppPerDefaultGroupLangCode.OWNER.getCode());
        OWNER_PER_GROUP.setName(AppPerDefaultGroupLangCode.OWNER.getName());
        OWNER_PER_GROUP.setRemake(AppPerDefaultGroupLangCode.OWNER.getDesc());
        OWNER_PER_GROUP.setReadOnly(1);

        // 项目成员
        PROJECT_MEMBER_PER_GROUP.setId(5L);
        PROJECT_MEMBER_PER_GROUP.setLangCode(AppPerDefaultGroupLangCode.PROJECT_MEMBER.getCode());
        PROJECT_MEMBER_PER_GROUP.setName(AppPerDefaultGroupLangCode.PROJECT_MEMBER.getName());
        PROJECT_MEMBER_PER_GROUP.setRemake(AppPerDefaultGroupLangCode.PROJECT_MEMBER.getDesc());
        PROJECT_MEMBER_PER_GROUP.setReadOnly(1);
    }
}