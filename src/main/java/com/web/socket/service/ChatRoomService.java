package com.web.socket.service;

import com.web.socket.dto.*;
import com.web.socket.dto.request.MessageRequest;
import com.web.socket.dto.response.MessageResponse;
import com.web.socket.entity.ChatRoom;
import com.web.socket.entity.MessageHistory;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface ChatRoomService {
    List<ChatRoomSummaryDTO> getChatRoomSummary() ;
    MessageResponse pushMessageToChatRoom(MessageRequest messageRequest, String chatRoomId);
    List<MessageStatusDTO> markReadMessages(String chatRoomId);
    List<MessageStatusDTO> markDeliveredMessages(String chatRoomId);
    void broadcastOfflineStatus();
    void broadcastOnlineStatus();
    Map<String, Object> createGroup(GroupCreationDTO groupCreationDTO);
    ChatRoomDetailDTO getChatRoomDetailz(String chatRoomId);
}
