package com.and1ss.private_chat_service.services;

import com.and1ss.private_chat_service.model.AccountInfo;

import java.util.UUID;

public interface UserService {
        AccountInfo authorizeUserByAccessToken(String accessToken);
        AccountInfo findUserById(UUID id);
}
