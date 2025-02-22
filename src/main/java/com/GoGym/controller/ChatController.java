package com.GoGym.controller;

import com.GoGym.dto.ChatRoomDTO;

import com.GoGym.dto.MessageDTO;
import com.GoGym.module.ChatRoom;
import com.GoGym.module.Message;
import com.GoGym.module.User;
import com.GoGym.repository.ChatRoomRepository;
import com.GoGym.repository.MessageRepository;
import com.GoGym.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatRoomRepository chatRoomRepository, MessageRepository messageRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

@GetMapping
@ResponseBody
public List<ChatRoomDTO> getChatRooms(Principal principal) {
    User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

    return chatRoomRepository.findByUserOrTrainer(user, user)
            .stream()
            .map(chatRoom -> {
                boolean isClient = chatRoom.getUser().equals(user);
                return new ChatRoomDTO(
                        chatRoom.getId(),
                        isClient ? chatRoom.getUser().getIdUser() : chatRoom.getTrainer().getIdUser(),
                        isClient ? chatRoom.getTrainer().getFirstName() + " " + chatRoom.getTrainer().getSecondName()
                                : chatRoom.getUser().getFirstName() + " " + chatRoom.getUser().getSecondName()
                );
            })
            .toList();
}




    @GetMapping("/{chatRoomId}/messages")
    @ResponseBody
    public List<MessageDTO> getChatMessages(@PathVariable Long chatRoomId) {
        List<Message> messages = messageRepository.findByChatRoomId(chatRoomId);
        return messages.stream()
                .map(msg -> new MessageDTO(msg.getId(), msg.getSender(), msg.getMessage(), msg.getSentAt()))
                .toList();
    }



    @PostMapping("/start")
    public ResponseEntity<ChatRoom> startChat(@RequestParam Long trainerId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono trenera"));

        Optional<ChatRoom> existingChat = chatRoomRepository.findByUser_IdUserAndTrainer_IdUser(user.getIdUser(), trainerId);
        if (existingChat.isPresent()) {
            return ResponseEntity.ok(existingChat.get());
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUser(user);
        chatRoom.setTrainer(trainer);
        chatRoomRepository.save(chatRoom);

        return ResponseEntity.ok(chatRoom);
    }

    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(@DestinationVariable Long chatRoomId, @Payload MessageDTO messageDTO, Principal principal) {
        User sender = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pokoju chatu"));

        Message message = new Message();
        message.setSender(sender);  // ✅ Ustawienie nadawcy
        message.setChatRoom(chatRoom);
        message.setMessage(messageDTO.getMessage());
        message.setSentAt(LocalDateTime.now());

        messageRepository.save(message);

        // 🔥 Konwersja wiadomości na DTO przed wysłaniem do WebSocket
        MessageDTO responseMessage = new MessageDTO(
                message.getId(),
                sender,
                message.getMessage(),
                message.getSentAt()
        );

        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, responseMessage);
    }

    @DeleteMapping("/{chatRoomId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long chatRoomId,
                                           @PathVariable Long messageId,
                                           Principal principal) {
        // Pobranie aktualnego użytkownika
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        // Pobranie wiadomości
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wiadomości"));

        // Sprawdzenie, czy wiadomość należy do pokoju
        if (!message.getChatRoom().getId().equals(chatRoomId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Niepoprawny pokój chatu");
        }

        // Weryfikacja, czy aktualny użytkownik jest nadawcą
        if (!message.getSender().equals(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Możesz usuwać tylko własne wiadomości");
        }

        // Usuwamy wiadomość
        messageRepository.delete(message);

        // Wysyłamy zdarzenie o usunięciu wiadomości do kanału WebSocket
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId + "/delete", messageId);

        return ResponseEntity.ok("Wiadomość usunięta");
    }






}

