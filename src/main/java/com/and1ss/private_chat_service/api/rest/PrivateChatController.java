package com.and1ss.private_chat_service.api.rest;

import com.and1ss.private_chat_service.api.dto.PrivateChatCreationDTO;
import com.and1ss.private_chat_service.api.dto.PrivateChatRetrievalDTO;
import com.and1ss.private_chat_service.api.dto.PrivateMessageCreationDTO;
import com.and1ss.private_chat_service.api.dto.PrivateMessageRetrievalDTO;
import com.and1ss.private_chat_service.exceptions.BadRequestException;
import com.and1ss.private_chat_service.model.AccountInfo;
import com.and1ss.private_chat_service.model.PrivateChat;
import com.and1ss.private_chat_service.model.PrivateMessage;
import com.and1ss.private_chat_service.services.PrivateChatMessageService;
import com.and1ss.private_chat_service.services.PrivateChatService;
import com.and1ss.private_chat_service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chats/private")
public class PrivateChatController {
    @Autowired
    PrivateChatService privateChatService;

    @Autowired
    PrivateChatMessageService privateChatMessageService;

    @Autowired
    UserService userService;

    @GetMapping("/all")
    public List<PrivateChatRetrievalDTO> getAllPrivateChats(@RequestHeader("Authorization") String token) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);

        List<PrivateChat> privateChats =
                privateChatService.getAllPrivateChatsForUser(authorizedUser);

        return privateChats.stream()
                .map(PrivateChatRetrievalDTO::fromPrivateChat)
                .collect(Collectors.toList());
    }

    @GetMapping("/{chat_id}")
    public PrivateChatRetrievalDTO getPrivateChat(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);
        return PrivateChatRetrievalDTO.fromPrivateChat(privateChat);
    }

    @PostMapping
    public PrivateChatRetrievalDTO createPrivateChat(
            @RequestBody PrivateChatCreationDTO chatCreationDTO,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        AccountInfo user2 = userService.findUserById(chatCreationDTO.getUserId());

        if (user2.equals(authorizedUser)) {
            throw new BadRequestException("User can not create private chats with himself");
        }

        PrivateChat toBeCreated = PrivateChat.builder()
                .user1Id(authorizedUser.getId())
                .user2Id(user2.getId())
                .build();

        PrivateChat createdChat = privateChatService
                .createPrivateChat(toBeCreated, authorizedUser);

        return PrivateChatRetrievalDTO.fromPrivateChat(createdChat);
    }

    @PostMapping("/{chat_id}/messages")
    public PrivateMessageRetrievalDTO addMessageToPrivateChat(
            @RequestBody PrivateMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);

        PrivateMessage message = PrivateMessage.builder()
                .authorId(authorizedUser.getId())
                .chatId(chatId)
                .contents(messageCreationDTO.getContents())
                .build();

        PrivateMessage savedMessage = privateChatMessageService
                .addMessage(privateChat, message, authorizedUser);

        return PrivateMessageRetrievalDTO.fromPrivateMessage(savedMessage);
    }


    @GetMapping("/{chat_id}/messages")
    public List<PrivateMessageRetrievalDTO> getPrivateChatMessages(
            @PathVariable("chat_id") UUID chatId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);
        List<PrivateMessage> messages = privateChatMessageService
                .getAllMessages(privateChat, authorizedUser);

        return messages.stream()
                .map(PrivateMessageRetrievalDTO::fromPrivateMessage)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{chat_id}/messages/{message_id}")
    public PrivateMessageRetrievalDTO patchMessageOfPrivateChat(
            @RequestBody PrivateMessageCreationDTO messageCreationDTO,
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);

        PrivateMessage message = PrivateMessage.builder()
                .id(messageId)
                .authorId(authorizedUser.getId())
                .chatId(chatId)
                .contents(messageCreationDTO.getContents())
                .build();

        PrivateMessage savedMessage = privateChatMessageService
                .patchMessage(privateChat, message, authorizedUser);

        return PrivateMessageRetrievalDTO.fromPrivateMessage(savedMessage);
    }

    @DeleteMapping("/{chat_id}/messages/{message_id}")
    public void patchMessageOfPrivateChat(
            @PathVariable("chat_id") UUID chatId,
            @PathVariable("message_id") UUID messageId,
            @RequestHeader("Authorization") String token
    ) {
        AccountInfo authorizedUser = authorizeUserByBearerToken(token);
        PrivateChat privateChat = privateChatService
                .getPrivateChatById(chatId, authorizedUser);
        PrivateMessage message = privateChatMessageService.getMessageById(messageId);

        privateChatMessageService.deleteMessage(privateChat, message, authorizedUser);
    }


    private AccountInfo authorizeUserByBearerToken(String token) {
        String parsedAccessToken = token.replaceFirst("Bearer\\s", "");
        return userService.authorizeUserByAccessToken(parsedAccessToken);
    }
}