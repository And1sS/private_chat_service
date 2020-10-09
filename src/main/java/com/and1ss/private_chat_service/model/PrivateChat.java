package com.and1ss.private_chat_service.model;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "private_chat")
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class PrivateChat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    protected UUID id;

    @NonNull
    @Column(name = "user_1_id")
    private UUID user1Id;

    @NonNull
    @Column(name = "user_2_id")
    private UUID user2Id;

    public PrivateChat(AccountInfo user1, AccountInfo user2) {
        this.user1Id = user1.getId();
        this.user2Id = user2.getId();
    }
}
