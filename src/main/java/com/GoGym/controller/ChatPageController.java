package com.GoGym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatPageController {

    @GetMapping("/chat")
    public String chatPage() {
        return "chat_frontend"; // Nazwa pliku HTML bez rozszerzenia, musi być w `src/main/resources/templates`
    }
}
