package com.GoGym.controller;

import com.GoGym.dto.ChatRoomDTO;

import com.GoGym.dto.MessageDTO;
import com.GoGym.module.ChatRoom;
import com.GoGym.module.Message;
import com.GoGym.module.User;
import com.GoGym.repository.ChatRoomRepository;
import com.GoGym.repository.MessageRepository;
import com.GoGym.repository.UserRepository;
import com.GoGym.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
@RequestMapping("/chat")
public class ChatController {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public ChatController(ChatRoomRepository chatRoomRepository, MessageRepository messageRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    @GetMapping
    @ResponseBody
    public List<ChatRoomDTO> getChatRooms(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        boolean isAdmin = user.getUserType() == User.UserType.ADMIN;

        return chatService.getChatRoomsForUser(user)
                .stream()
                .map(chatRoom -> {
                    boolean isAdminChat = chatRoom.getTrainer().getUserType() == User.UserType.ADMIN
                            || chatRoom.getUser().getUserType() == User.UserType.ADMIN;

                    String chatName;
                    if (isAdmin) {
                        // Jeśli admin, pokaż prawdziwe imię i nazwisko użytkownika
                        chatName = chatRoom.getUser().getUserType() == User.UserType.ADMIN
                                ? chatRoom.getTrainer().getFirstName() + " " + chatRoom.getTrainer().getSecondName()
                                : chatRoom.getUser().getFirstName() + " " + chatRoom.getUser().getSecondName();
                    } else {
                        // Dla użytkowników zamiast "Admin Admin" pokażemy "Administracja"
                        chatName = isAdminChat ? "Administracja"
                                : (chatRoom.getUser().equals(user)
                                ? chatRoom.getTrainer().getFirstName() + " " + chatRoom.getTrainer().getSecondName()
                                : chatRoom.getUser().getFirstName() + " " + chatRoom.getUser().getSecondName());
                    }

                    long unreadCount = messageRepository.countUnreadMessages(chatRoom.getId(), user.getIdUser());
                    return new ChatRoomDTO(
                            chatRoom.getId(),
                            isAdminChat ? -1 : (chatRoom.getUser().equals(user) ? chatRoom.getTrainer().getIdUser() : chatRoom.getUser().getIdUser()),
                            chatName,
                            unreadCount
                    );
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{chatRoomId}/messages")
    @ResponseBody
    public List<MessageDTO> getChatMessages(@PathVariable Long chatRoomId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        List<Message> messages = messageRepository.findByChatRoomId(chatRoomId);

        // Oznacz nieodczytane wiadomości jako przeczytane, jeśli nie zostały wysłane przez aktualnego użytkownika
        List<Message> unreadMessages = messageRepository.findUnreadMessages(chatRoomId, user.getIdUser());
        for (Message message : unreadMessages) {
            message.setRead(true);
        }
        messageRepository.saveAll(unreadMessages);

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

        // 🛠 POPRAWKA: Pobierz odbiorcę poprawnie!
        User recipient;
        if (chatRoom.getUser().getIdUser().equals(sender.getIdUser())) {
            recipient = chatRoom.getTrainer();
        } else {
            recipient = chatRoom.getUser();
        }


        Message message = new Message();
        message.setSender(sender);
        message.setChatRoom(chatRoom);
        message.setMessage(messageDTO.getMessage());
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        messageRepository.save(message);

        // Tworzymy DTO wiadomości do wysłania
        MessageDTO responseMessage = new MessageDTO(
                message.getId(),
                sender,
                message.getMessage(),
                message.getSentAt()
        );

        // Wysyłamy wiadomość do pokoju
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, responseMessage);

        System.out.println("📨 Wiadomość wysłana do pokoju " + chatRoomId);
        System.out.println("👤 Nadawca: " + sender.getFirstName());
        System.out.println("🎯 Odbiorca: " + recipient.getFirstName());

        // 🔥 Aktualizujemy liczbę nieodczytanych wiadomości dla odbiorcy
        List<Message> unreadMessages = messageRepository.findUnreadMessages(chatRoomId, recipient.getIdUser());

        System.out.println("📬 Nieprzeczytane wiadomości:");
        for (Message msg : unreadMessages) {
            System.out.println("📝 ID: " + msg.getId() + " | Nadawca: " + msg.getSender().getFirstName() + " | Treść: " + msg.getMessage());
        }

        long unreadCountForRecipient = messageRepository.countUnreadMessages(chatRoomId, recipient.getIdUser());

        messagingTemplate.convertAndSend("/topic/chat/updateUnread", new ChatRoomDTO(
                chatRoom.getId(),
                recipient.getIdUser(),
                recipient.getFirstName() + " " + recipient.getSecondName(),
                unreadCountForRecipient
        ));

        System.out.println("📬 Nieprzeczytane wiadomości dla odbiorcy (" + recipient.getFirstName() + "): " + unreadCountForRecipient);
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

    @PostMapping("/{chatRoomId}/read")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable Long chatRoomId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika"));

        List<Message> unreadMessages = messageRepository.findUnreadMessages(chatRoomId, user.getIdUser());
        unreadMessages.forEach(msg -> msg.setRead(true));
        messageRepository.saveAll(unreadMessages);

        // 🔥 Powiadamiamy o aktualizacji liczby nieprzeczytanych wiadomości
        long unreadCount = messageRepository.countUnreadMessages(chatRoomId, user.getIdUser());

        // 🔔 Wysłanie powiadomienia o odczytaniu wiadomości
        messagingTemplate.convertAndSend("/topic/chat/updateUnread", new ChatRoomDTO(
                chatRoomId,
                user.getIdUser(),
                user.getFirstName() + " " + user.getSecondName(),
                unreadCount
        ));

        System.out.println("✅ Wiadomości oznaczone jako przeczytane w pokoju: " + chatRoomId + " przez: " + user.getFirstName());

        return ResponseEntity.ok("Wiadomości oznaczone jako przeczytane");
    }

    @PostMapping("/admin/init-chats")
    public ResponseEntity<String> initializeAdminChats() {
        chatService.createAdminChatsOnStartup();
        return ResponseEntity.ok("Wszystkie czaty administratora zostały zainicjalizowane.");
    }


}

