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

    // ✅ Konstruktor używany przez WebSocket do wysyłania wiadomości
    public MessageDTO(Long id, User sender, String message, LocalDateTime sentAt) {
        this.id = id;
        this.senderId = sender.getIdUser();
        this.senderName = sender.getFirstName();
        this.message = message;
        this.sentAt = sentAt;
    }

    // ✅ Konstruktor używany do deserializacji JSON (Spring potrzebuje tego!)
    public MessageDTO() {}

    // ✅ Konstruktor używany do odbioru wiadomości (bez User obiektu)
    public MessageDTO(Long senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }
}
