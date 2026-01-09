package com.dezonnov1.SpringGuideBotMinecraft.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BotState state;

    private LocalDateTime updatedAt;
}
