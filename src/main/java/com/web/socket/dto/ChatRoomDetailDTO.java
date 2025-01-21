package com.web.socket.dto;


import com.web.socket.entity.RoomType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class ChatRoomDetailDTO {
    private String chatRoomId;
    private RoomType roomType;
    private List<MessageHistoryDTO> messageHistory;
    private List<GeneralUserProfileDTO> members;
    @Getter
    @Setter
    @Builder
    public static class MessageHistoryDTO {
        private Double day;
        List<MessageDTO> messages;
    }
}
