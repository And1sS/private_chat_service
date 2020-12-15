package com.and1ss.private_chat_service.api.mqtt.dto;

import lombok.Data;

@Data
public class MyMqttMessage<T> {
    private String token;

    T payload;
}
