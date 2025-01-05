package com.web.socket.dto;


import com.web.socket.entity.RoomType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@Setter
@Getter
public class ChatRoomDetailDTO {
    private String chatRoomId;
    private RoomType roomType;
    private Map<Double, List<MessageDTO>> messageHistory;
}
