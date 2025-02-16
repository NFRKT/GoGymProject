package com.GoGym.controller;

import com.GoGym.module.ChatMessage;
import com.GoGym.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        ChatMessage savedMessage = chatService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/chat/" + message.getReceiver().getIdUser(), savedMessage);
        return savedMessage;
    }
}
