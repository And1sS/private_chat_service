package com.and1ss.private_chat_service.api.dto;

import com.and1ss.private_chat_service.model.PrivateChat;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivateChatRetrievalDTO {
    @NonNull
    private UUID id;

    private UUID user1Id;

    private UUID user2Id;

    public static PrivateChatRetrievalDTO fromPrivateChat(PrivateChat privateChat) {
        return PrivateChatRetrievalDTO.builder()
                .id(privateChat.getId())
                .user1Id(privateChat.getUser1Id())
                .user2Id(privateChat.getUser2Id())
                .build();
    }
}