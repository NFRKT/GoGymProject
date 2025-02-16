package com.GoGym.service;

import com.GoGym.module.ChatMessage;
import com.GoGym.module.User;
import com.GoGym.repository.ChatMessageRepository;
import com.GoGym.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository; // Dodaj repozytorium użytkowników

    public ChatService(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    public ChatMessage saveMessage(ChatMessage message) {
        if (message.getSender() == null || message.getSender().getIdUser() == null) {
            throw new IllegalArgumentException("Sender ID is null");
        }
        if (message.getReceiver() == null || message.getReceiver().getIdUser() == null) {
            throw new IllegalArgumentException("Receiver ID is null");
        }

        User sender = userRepository.findById(message.getSender().getIdUser())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(message.getReceiver().getIdUser())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(message);
    }

}
