package com.example.chatapp.messages;

import com.example.chatapp.users.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "messages")
@Schema(description = "Сообщение", example =
        """
            {
                "id": 1,
                "content": "Привет!",
                "user": {
                    "id": 1,
                    "username": "nikita",
                    "email": "nikita@example.com"
                },
                "createdAt": "2025-03-19T10:30:00"
            }
            """
)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Schema(description = "Текст сообщения")
    private String content;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @Schema(description = "Пользователь который отправил сообщение")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    @Schema(description = "Пользователь которому отправили сообщение")
    private User receiver;

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public String getContent() {
        return content;
    }

    public Long getId() {
        return id;
    }

    @Schema(description = "Время создания")
    private LocalDateTime createdAt;

    protected Message() {}

    public Message(Long id, String content, User owner, User receiver) {
        this.id = id;
        this.content = content;
        this.owner = owner;
        this.receiver = receiver;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return Objects.equals(id, message.id) &&
                Objects.equals(content, message.content) &&
                Objects.equals(owner, message.owner) &&
                Objects.equals(receiver, message.receiver);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, content, owner, receiver);
    }

}
