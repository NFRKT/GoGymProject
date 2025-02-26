package com.GoGym.service;

import com.GoGym.module.ChatRoom;
import com.GoGym.module.User;
import com.GoGym.repository.ChatRoomRepository;
import com.GoGym.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @PostConstruct
    public void createAdminChatsOnStartup() {
        Optional<User> adminOpt = userRepository.findByUserType(User.UserType.ADMIN).stream().findFirst();

        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();
            List<User> users = userRepository.findAll().stream()
                    .filter(user -> !user.getUserType().equals(User.UserType.ADMIN)) // Pomijamy admina
                    .toList();

            int createdChats = 0;
            for (User user : users) {
                Optional<ChatRoom> existingChat = chatRoomRepository.findByUser_IdUserAndTrainer_IdUser(user.getIdUser(), admin.getIdUser());
                if (existingChat.isEmpty()) {
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setUser(user);
                    chatRoom.setTrainer(admin);
                    chatRoomRepository.save(chatRoom);
                    createdChats++;
                }
            }

            System.out.println("✅ Wszystkie czaty administratora zostały utworzone: " + createdChats + " nowych rozmów.");
        } else {
            System.err.println("⚠️ Nie znaleziono administratora w bazie danych!");
        }
    }

    public void createChatWithAdmin(User newUser) {
        Optional<User> adminOpt = userRepository.findByUserType(User.UserType.ADMIN).stream().findFirst();

        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();

            Optional<ChatRoom> existingChat = chatRoomRepository.findByUser_IdUserAndTrainer_IdUser(newUser.getIdUser(), admin.getIdUser());
            if (existingChat.isEmpty()) {
                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setUser(newUser);
                chatRoom.setTrainer(admin);
                chatRoomRepository.save(chatRoom);
            }
        } else {
            System.err.println("⚠️ Nie znaleziono administratora w bazie danych!");
        }
    }

    public List<ChatRoom> getChatRoomsForUser(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserOrTrainer(user, user);

        // Podział czatów na "Administracja" oraz resztę
        List<ChatRoom> adminChatRooms = chatRooms.stream()
                .filter(chat -> chat.getTrainer().getUserType() == User.UserType.ADMIN || chat.getUser().getUserType() == User.UserType.ADMIN)
                .collect(Collectors.toList());

        List<ChatRoom> otherChats = chatRooms.stream()
                .filter(chat -> chat.getTrainer().getUserType() != User.UserType.ADMIN && chat.getUser().getUserType() != User.UserType.ADMIN)
                .collect(Collectors.toList());

        // Najpierw zwracamy chat z Administracją, potem resztę
        adminChatRooms.addAll(otherChats);
        return adminChatRooms;
    }
}
