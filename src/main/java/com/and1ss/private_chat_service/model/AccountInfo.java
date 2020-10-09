package com.and1ss.private_chat_service.model;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
    @NonNull
    private UUID id;

    @NonNull
    private String name;

    @NonNull
    private String surname;
}
