package com.web.socket.dto;

import com.web.socket.entity.ChatRoom;
import com.web.socket.entity.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SummaryChatRoomProjection {
    public String chatRoomId;
    public Message latestMessage;
    public ChatRoom chatRoomInfo;
}
