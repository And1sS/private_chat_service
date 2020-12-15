package com.and1ss.private_chat_service.api.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateMessageCreationDTO {
    private UUID chatId;

    @NonNull
    private String contents;
}
