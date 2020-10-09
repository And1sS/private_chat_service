package com.and1ss.private_chat_service.services;

import com.and1ss.private_chat_service.model.AccountInfo;
import com.and1ss.private_chat_service.model.PrivateChat;
import com.and1ss.private_chat_service.model.PrivateMessage;

import java.util.List;
import java.util.UUID;

public interface PrivateChatMessageService {
    List<PrivateMessage> getAllMessages(PrivateChat privateChat, AccountInfo author);
    PrivateMessage addMessage(PrivateChat privateChat, PrivateMessage message, AccountInfo author);
    PrivateMessage patchMessage(PrivateChat privateChat, PrivateMessage message, AccountInfo author);
    PrivateMessage getMessageById(UUID id);
    void deleteMessage(PrivateChat privateChat, PrivateMessage message, AccountInfo author);
}
