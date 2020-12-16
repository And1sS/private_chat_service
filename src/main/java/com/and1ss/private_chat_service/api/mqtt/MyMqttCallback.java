package com.and1ss.private_chat_service.api.mqtt;

import com.and1ss.private_chat_service.api.mqtt.dto.MyMqttMessage;
import com.and1ss.private_chat_service.api.rest.dto.PrivateChatCreationDTO;
import com.and1ss.private_chat_service.api.rest.dto.PrivateMessageCreationDTO;
import com.and1ss.private_chat_service.exceptions.BadRequestException;
import com.and1ss.private_chat_service.model.PrivateChat;
import com.and1ss.private_chat_service.model.PrivateMessage;
import com.and1ss.private_chat_service.services.PrivateChatMessageService;
import com.and1ss.private_chat_service.services.PrivateChatService;
import com.and1ss.private_chat_service.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class MyMqttCallback implements MqttCallback {
    private final PrivateChatService privateChatService;
    private final PrivateChatMessageService privateChatMessageService;
    private final UserService userService;

    @Autowired
    public MyMqttCallback(
            PrivateChatService privateChatService,
            PrivateChatMessageService privateChatMessageService,
            UserService userService
    ) {
        this.privateChatService = privateChatService;
        this.privateChatMessageService = privateChatMessageService;
        this.userService = userService;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        log.info("Message arrived on topic: " + topic + ", message: " + message);

        try {
            switch (topic) {
                case "/private-chat-service/new-message":
                    handleNewMessageCreation(message);
                    break;

                case "/private-chat-service/new-chat":
                    handleNewChatCreation(message);
                    break;

                default: log.error("Message on invalid topic arrived!");
            }
        } catch (Exception e) {
            log.error("topic: " + topic + " " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    private void handleNewChatCreation(MqttMessage message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        var typeRef = new TypeReference<MyMqttMessage<PrivateChatCreationDTO>>() {};

        String messageAsString = new String(message.getPayload(), StandardCharsets.UTF_8);
        MyMqttMessage<PrivateChatCreationDTO> object = mapper.readValue(messageAsString, typeRef);

        var stringToken = (String) object.getToken();
        if (stringToken == null) {
            throw new BadRequestException("Access token is not specified");
        }
        final var authorizedUser = userService.authorizeUserByAccessToken(stringToken);

        var payload = object.getPayload();
        if (payload == null) {
            throw new BadRequestException("Incoming message payload is not present");
        }
        final var chatCreationDTO = mapper.convertValue(
                payload, PrivateChatCreationDTO.class
        );

        PrivateChat toBeCreated = PrivateChat.builder()
                .user1Id(authorizedUser.getId())
                .user2Id(chatCreationDTO.getUserId())
                .build();

        PrivateChat createdChat = privateChatService
                .createPrivateChat(toBeCreated, authorizedUser);

        log.info("Created chat: " + createdChat.toString());
    }

    private void handleNewMessageCreation(MqttMessage message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        var typeRef = new TypeReference<MyMqttMessage<PrivateMessageCreationDTO>>() {};

        String messageAsString = new String(message.getPayload(), StandardCharsets.UTF_8);
        MyMqttMessage<PrivateMessageCreationDTO> object = mapper.readValue(messageAsString, typeRef);

        var stringToken = (String) object.getToken();
        if (stringToken == null) {
            throw new BadRequestException("Access token is not specified");
        }
        final var authorizedUser = userService.authorizeUserByAccessToken(stringToken);

        var payload = object.getPayload();
        if (payload == null) {
            throw new BadRequestException("Incoming message payload is not present");
        }
        final var messageDTO = mapper.convertValue(
                payload, PrivateMessageCreationDTO.class
        );

        final var privateChat = privateChatService.getPrivateChatById(
                messageDTO.getChatId(),
                authorizedUser
        );
        final var privateMessage = PrivateMessage.builder()
                .authorId(authorizedUser.getId())
                .chatId(privateChat.getId())
                .contents(messageDTO.getContents())
                .build();

        final var savedMessage = privateChatMessageService
                .addMessage(privateChat, privateMessage, authorizedUser);

        log.info("Created message: " + savedMessage.toString());
    }
}