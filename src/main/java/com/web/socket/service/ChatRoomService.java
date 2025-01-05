package com.web.socket.service;

import com.web.socket.dto.response.ChatRoomSummaryDTO;
import com.web.socket.dto.response.MessageDTO;
import com.web.socket.dto.response.MessageStatusDTO;

import java.util.List;

public interface ChatRoomService {
    List<ChatRoomSummaryDTO> getChatRoomSummary();
    MessageDTO pushMessageToChatRoom(MessageDTO messageDTO, String chatRoomId);
    List<MessageStatusDTO> markReadMessage(List<String> messagesId, String chatRoomId);
}
