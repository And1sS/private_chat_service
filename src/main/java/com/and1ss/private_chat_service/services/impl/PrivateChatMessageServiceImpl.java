package com.and1ss.private_chat_service.services.impl;

import com.and1ss.private_chat_service.exceptions.BadRequestException;
import com.and1ss.private_chat_service.exceptions.UnauthorizedException;
import com.and1ss.private_chat_service.model.AccountInfo;
import com.and1ss.private_chat_service.model.PrivateChat;
import com.and1ss.private_chat_service.model.PrivateMessage;
import com.and1ss.private_chat_service.services.PrivateChatMessageService;
import com.and1ss.private_chat_service.services.PrivateChatService;
import com.and1ss.private_chat_service.services.impl.repos.PrivateChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PrivateChatMessageServiceImpl implements PrivateChatMessageService {
    @Autowired
    private PrivateChatService privateChatService;

    @Autowired
    private PrivateChatMessageRepository privateChatMessageRepository;

    @Override
    public List<PrivateMessage> getAllMessages(PrivateChat privateChat, AccountInfo author) {
        if (!privateChatService.userMemberOfPrivateChat(privateChat, author)) {
            throw new UnauthorizedException("This user can not view messages of this chat");
        }

        return privateChatMessageRepository
                .getPrivateMessagesByChatId(privateChat.getId());
    }

    @Override
    public PrivateMessage addMessage(
            PrivateChat privateChat,
            PrivateMessage message,
            AccountInfo author
    ) {
        if (!privateChatService.userMemberOfPrivateChat(privateChat, author)) {
            throw new UnauthorizedException("This user can not write messages to this chat");
        }

        if (message.getContents().isEmpty()) {
            throw new BadRequestException("Message contents must not be empty");
        }

        return privateChatMessageRepository.save(message);
    }

    @Override
    public PrivateMessage patchMessage(
            PrivateChat privateChat,
            PrivateMessage message,
            AccountInfo author
    ) {
        try {
            privateChatMessageRepository.getOne(message.getId());
        } catch (Exception e) {
            throw new BadRequestException("Invalid message Id");
        }

        if (!message.getAuthorId().equals(author.getId())) {
            throw new UnauthorizedException("This user can not patch this message");
        }

        return privateChatMessageRepository.save(message);
    }

    @Override
    public PrivateMessage getMessageById(UUID id) {
        try {
            return privateChatMessageRepository.getOne(id);
        } catch (Exception e) {
            throw new BadRequestException("Invalid message Id");
        }
    }

    @Override
    public void deleteMessage(
            PrivateChat privateChat,
            PrivateMessage message,
            AccountInfo author
    ) {
        try {
            privateChatMessageRepository.getOne(message.getId());
        } catch (Exception e) {
            throw new BadRequestException("Invalid message Id");
        }

        if (!privateChatService
                .userMemberOfPrivateChat(privateChat, author)) {
            throw new UnauthorizedException("This user can not delete this message");
        }
        privateChatMessageRepository.delete(message);
    }
}
