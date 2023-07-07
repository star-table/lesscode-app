package com.polaris.lesscode.app.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polaris.lesscode.app.bo.Event;
import com.polaris.lesscode.gopush.internal.api.GoPushApi;
import com.polaris.lesscode.gopush.internal.req.PushMqttReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoPushServiceImpl implements GoPushService {
    @Autowired
    private GoPushApi goPushApi;

    public void pushMqtt(Long orgId, Long projectId, Event event) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        goPushApi.pushMqtt(new PushMqttReq(null, mapper.writeValueAsBytes(event), orgId, projectId));
    }
}
