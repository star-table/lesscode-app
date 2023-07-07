package com.polaris.lesscode.app.req;

import lombok.Data;

import java.util.List;

@Data
public class DelAppMemberReq {

    private List<String> memberIds;
}
