package com.web.socket.service;

import com.web.socket.dto.ChatRoomDetailDTO;
import com.web.socket.dto.ChatRoomSummaryDTO;
import com.web.socket.dto.MessageDTO;
import com.web.socket.dto.MessageStatusDTO;

import java.util.List;

public interface ChatRoomService {
    List<ChatRoomSummaryDTO> getChatRoomSummary() ;
    ChatRoomDetailDTO getChatRoomDetail(String chatRoomId);
    MessageDTO pushMessageToChatRoom(MessageDTO messageDTO, String chatRoomId) ;
    List<MessageStatusDTO> markReadMessages(String chatRoomId) ;
    List<MessageStatusDTO> markDeliveredMessages(String chatRoomId) ;
    void broadcastOfflineStatus();
    void broadcastOnlineStatus();
}
