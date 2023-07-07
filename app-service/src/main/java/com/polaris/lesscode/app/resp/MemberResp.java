package com.polaris.lesscode.app.resp;

import lombok.Data;

/**
 * 成员
 *
 * @author Nico
 * @date 2021/2/20 18:53
 */
@Data
public class MemberResp {

    private Long id;

    private String name;

    private String avatar;

    private String type;

    private Integer status;

    private Integer isDelete;

    public MemberResp(Long id, String name, String avatar, String type, Integer status, Integer isDelete) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
        this.status = status;
        this.isDelete = isDelete;
    }

    public MemberResp() {
    }

}
