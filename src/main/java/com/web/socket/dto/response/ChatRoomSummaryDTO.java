package com.web.socket.dto.response;

import com.web.socket.entity.RoomType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ChatRoomSummaryDTO {
    private String chatRoomId;
    private RoomType roomType;
    private MessageDTO lastestMessage;
    private List<MessageStatusDTO> unreadMessage;
    private GeneralUserProfileDTO userProfile;
}
