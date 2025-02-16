package com.GoGym.dto;

import com.GoGym.module.ChatRoom;
import lombok.Data;

@Data
public class ChatRoomDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long trainerId;
    private String trainerName;

    public ChatRoomDTO(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.userId = chatRoom.getUser().getIdUser();
        this.userName = chatRoom.getUser().getFirstName() + " " + chatRoom.getUser().getSecondName();
        this.trainerId = chatRoom.getTrainer().getIdUser();
        this.trainerName = chatRoom.getTrainer().getFirstName() + " " + chatRoom.getTrainer().getSecondName();
    }
}
