package com.polaris.lesscode.app.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Event {
    @JsonProperty("category")
    private String category;

    @JsonProperty("type")
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("traceId")
    private String traceId;

    @JsonProperty("payload")
    private Object payload;
}
