package com.polaris.lesscode.app.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polaris.lesscode.app.entity.AppView;
import lombok.Data;

@Data
public class ViewEvent {
    @JsonProperty("orgId")
    private Long orgId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("appId")
    private Long appId;

    @JsonProperty("projectId")
    private Long projectId;

    @JsonProperty("userId")
    private Long userId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("viewId")
    private Long viewId;

    @JsonProperty("new")
    private Object newData;
}
