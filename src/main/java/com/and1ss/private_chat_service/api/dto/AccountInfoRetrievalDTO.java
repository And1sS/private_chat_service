package com.and1ss.private_chat_service.api.dto;

import com.and1ss.private_chat_service.model.AccountInfo;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoRetrievalDTO {
    @NonNull
    private UUID id;

    @NonNull
    private String name;

    @NonNull
    private String surname;

    public static AccountInfoRetrievalDTO fromAccountInfo(AccountInfo user) {
        return AccountInfoRetrievalDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .build();
    }
}
