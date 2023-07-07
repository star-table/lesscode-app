package com.polaris.lesscode.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.polaris.lesscode.app.bo.Event;

public interface GoPushService {
    void pushMqtt(Long orgId, Long projectId, Event event) throws JsonProcessingException;
}
