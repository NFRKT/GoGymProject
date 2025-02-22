package com.GoGym.repository;

import com.GoGym.module.ChatRoom;
import com.GoGym.module.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUser_IdUserAndTrainer_IdUser(Long userId, Long trainerId);
    List<ChatRoom> findByUserOrTrainer(User user, User trainer);
    void deleteByUserAndTrainer(User user, User trainer);


}
