package com.and1ss.private_chat_service.api.dto;

import com.and1ss.private_chat_service.model.AccountInfo;
import com.and1ss.private_chat_service.model.PrivateMessage;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivateMessageRetrievalDTO {
    @NonNull
    private UUID id;

    private UUID authorId;

    @NonNull
    private UUID chatId;

    private String contents;

    private Timestamp createdAt;

    public static PrivateMessageRetrievalDTO
    fromPrivateMessage(PrivateMessage privateMessage) {

        return builder()
                .id(privateMessage.getId())
                .authorId(privateMessage.getAuthorId())
                .chatId(privateMessage.getChatId())
                .contents(privateMessage.getContents())
                .createdAt(privateMessage.getCreatedAt())
                .build();
    }
}