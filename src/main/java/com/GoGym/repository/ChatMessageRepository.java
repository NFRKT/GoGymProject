package com.GoGym.repository;

import com.GoGym.module.ChatMessage;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndReceiver(User sender, User receiver);
    List<ChatMessage> findByReceiver(User receiver);
}
