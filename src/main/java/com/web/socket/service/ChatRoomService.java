package com.web.socket.service;

import com.web.socket.dto.*;
import com.web.socket.dto.request.MessageRequest;
import com.web.socket.dto.response.MessageResponse;

import java.util.List;

public interface ChatRoomService {
    List<ChatRoomSummaryDTO> getChatRoomSummary() ;
    ChatRoomDetailDTO getChatRoomDetail(String chatRoomId);
    MessageResponse pushMessageToChatRoom(MessageRequest messageRequest, String chatRoomId);
    List<MessageStatusDTO> markReadMessages(String chatRoomId);
    List<MessageStatusDTO> markDeliveredMessages(String chatRoomId);
    void broadcastOfflineStatus();
    void broadcastOnlineStatus();
    void createGroup(GroupCreationDTO groupCreationDTO);
}
