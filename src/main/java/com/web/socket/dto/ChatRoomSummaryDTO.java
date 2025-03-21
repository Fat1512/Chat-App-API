package com.web.socket.dto;

import com.web.socket.dto.response.MessageResponse;
import com.web.socket.entity.RoomType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChatRoomSummaryDTO {
    private String chatRoomId;
    private RoomType roomType;
    private MessageResponse lastestMessage;
    private Integer totalUnreadMessages;
    private Object roomInfo;
}
