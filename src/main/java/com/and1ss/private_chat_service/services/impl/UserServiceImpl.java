package com.and1ss.private_chat_service.services.impl;

import com.and1ss.private_chat_service.exceptions.InternalServerException;
import com.and1ss.private_chat_service.services.impl.connections.UserServiceConnection;
import com.and1ss.private_chat_service.api.dto.AccountInfoRetrievalDTO;
import com.and1ss.private_chat_service.exceptions.UnauthorizedException;
import com.and1ss.private_chat_service.model.AccountInfo;
import com.and1ss.private_chat_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserServiceConnection userServiceConnection;

    @Override
    public AccountInfo authorizeUserByAccessToken(String accessToken) {
        AccountInfo accountInfo;
        try {
            AccountInfoRetrievalDTO retrievalDTO = userServiceConnection
                    .authorizeUserByAccessToken(accessToken).block();

            accountInfo = AccountInfo.builder()
                    .id(retrievalDTO.getId())
                    .name(retrievalDTO.getName())
                    .surname(retrievalDTO.getSurname())
                    .build();
        } catch (WebClientResponseException e) {
            switch(e.getRawStatusCode()) {
                case 401: throw new UnauthorizedException("User is not Authorized");
                default: throw new InternalServerException();
            }
        } catch (Exception e) {
            throw new InternalServerException();
        }

        return accountInfo;
    }

    @Override
    public AccountInfo findUserById(UUID id) {
        AccountInfoRetrievalDTO retrievalDTO = userServiceConnection
                .getUserById(id)
                .block();

        return AccountInfo.builder()
                .id(retrievalDTO.getId())
                .name(retrievalDTO.getName())
                .surname(retrievalDTO.getSurname())
                .build();
    }
}
