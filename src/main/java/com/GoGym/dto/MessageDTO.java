package com.GoGym.dto;

import com.GoGym.module.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long senderId;
    private String senderName;
    private String message;
    private LocalDateTime sentAt;

    public MessageDTO(Long id, User sender, String message, LocalDateTime sentAt) {
        this.id = id;
        this.senderId = sender.getIdUser();
        this.senderName = sender.getFirstName();
        this.message = message;
        this.sentAt = sentAt;
    }

    public MessageDTO() {}

    public MessageDTO(Long senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }
}
