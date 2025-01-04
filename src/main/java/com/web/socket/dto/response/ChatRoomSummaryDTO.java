package com.web.socket.dto.response;

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
    private Integer unreadMessageCount;
    private MessageDTO lastestMessage;
    private GeneralUserProfileDTO userProfile;
}
