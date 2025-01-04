package com.web.socket.service;

import com.web.socket.dto.response.ChatRoomSummaryDTO;
import com.web.socket.dto.response.MessageDTO;

import java.util.List;

public interface ChatRoomService {
    List<ChatRoomSummaryDTO> getChatRoomSummary();
    void pushMessageToChatRoom(MessageDTO messageDTO, String chatRoomId);
}
