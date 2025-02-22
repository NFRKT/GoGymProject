package com.GoGym.dto;

import lombok.Data;

@Data
public class ChatRoomDTO {
    private Long id;
    private Long targetUserId;
    private String targetUserName;
    private long unreadCount;

    public ChatRoomDTO(Long id, Long targetUserId, String targetUserName, long unreadCount) {
        this.id = id;
        this.targetUserId = targetUserId;
        this.targetUserName = targetUserName;
        this.unreadCount = unreadCount;
    }
}
