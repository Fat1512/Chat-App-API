package com.web.socket.service;

import com.web.socket.dto.ChatRoomDetailDTO;
import com.web.socket.entity.MessageHistory;

import java.util.List;

public interface MessageService {
    List<ChatRoomDetailDTO.MessageHistoryDTO> getMessages(String chatRoomId, Integer pageSize, Integer page, Integer paddingOffset);
}
