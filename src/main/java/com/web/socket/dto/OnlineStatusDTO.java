package com.web.socket.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
public class OnlineStatusDTO {
    private String chatRoomId;
    private String senderId;
    private Boolean status;
    private Double lastSeen;
}
