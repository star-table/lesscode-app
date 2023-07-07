package com.polaris.lesscode.app.bo;

import lombok.Data;

/**
 * 应用Action变更model
 *
 * @author Nico
 * @date 2021/1/22 10:53
 */
@Data
public class AppActionChanges {

    /**
     * 变动的字段key
     **/
    private String key;

    /**
     * 变动的字段name
     **/
    private String name;

    /**
     * 变动前内容
     **/
    private Object before;

    /**
     * 变动后内容
     **/
    private Object after;

    /**
     * 字段类型
     **/
    private String type;
}
