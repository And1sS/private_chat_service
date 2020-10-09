package com.and1ss.private_chat_service.services;


import com.and1ss.private_chat_service.model.AccountInfo;
import com.and1ss.private_chat_service.model.PrivateChat;

import java.util.List;
import java.util.UUID;

public interface PrivateChatService {
    PrivateChat createPrivateChat(PrivateChat chat, AccountInfo author);
    PrivateChat getPrivateChatById(UUID id, AccountInfo author);
    List<PrivateChat> getAllPrivateChatsForUser(AccountInfo user);
    List<PrivateChat> getPrivateChatsPageForUser(AccountInfo user);
    boolean userMemberOfPrivateChat(PrivateChat chat, AccountInfo author);
}
