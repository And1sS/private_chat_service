package com.and1ss.private_chat_service.services.impl.repos;

import com.and1ss.private_chat_service.model.PrivateChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("private_chat")
public interface PrivateChatRepository extends JpaRepository<PrivateChat, UUID> {
    @Query(
            value = "SELECT * FROM private_chat WHERE " +
            "user_1_id=:id OR user_2_id=:id",
            nativeQuery = true
    )
    List<PrivateChat> findPrivateChatsByUserId(UUID id);
}

