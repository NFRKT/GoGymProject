package com.GoGym.dto;

import lombok.Data;

@Data
public class ChatRoomDTO {
    private Long id;
    private Long targetUserId;
    private String targetUserName;

    public ChatRoomDTO(Long id, Long targetUserId, String targetUserName) {
        this.id = id;
        this.targetUserId = targetUserId;
        this.targetUserName = targetUserName;
    }
}
