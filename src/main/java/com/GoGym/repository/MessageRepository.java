package com.GoGym.repository;

import com.GoGym.module.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomId(Long chatRoomId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatRoom.id = :chatRoomId AND m.read = false AND m.sender.id <> :userId")
    long countUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :chatRoomId AND m.read = false AND m.sender.id <> :userId")
    List<Message> findUnreadMessages(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);
}
