package com.and1ss.private_chat_service.services.impl.repos;

import com.and1ss.private_chat_service.model.PrivateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("private_chat_message")
public interface PrivateChatMessageRepository extends JpaRepository<PrivateMessage, UUID> {
    List<PrivateMessage> getPrivateMessagesByChatId(UUID chatId);
}
