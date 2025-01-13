package com.web.socket.controller;

import com.web.socket.dto.MessageDTO;
import com.web.socket.dto.MessageStatusDTO;
import com.web.socket.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SocketController {

    private final ChatRoomService chatRoomService;

    @MessageMapping("/chatRoom/{chatRoomId}/sendMessage")
    @SendTo("/topic/chatRoom/{chatRoomId}/newMessages")
    public MessageDTO sendMessage(
            @DestinationVariable String chatRoomId,
            @RequestBody MessageDTO messageDTO) {
        MessageDTO messageResponse = chatRoomService.pushMessageToChatRoom(messageDTO, chatRoomId);
        return messageResponse;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/markAsRead")
    @SendTo("/topic/chatRoom/{chatRoomId}/message/status")
    public List<MessageStatusDTO> markAsReadMessage(
            @PathVariable String chatRoomId) {
        List<MessageStatusDTO> messageStatusDTOList = chatRoomService
                .markReadMessages(chatRoomId);
        return messageStatusDTOList;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/markAsDelivered")
    @SendTo("/topic/chatRoom/{chatRoomId}/message/status")
    public List<MessageStatusDTO> markAsDeliveredMessage(
            @PathVariable String chatRoomId) {
        List<MessageStatusDTO> messageStatusDTOList = chatRoomService.markDeliveredMessages(chatRoomId);
        return messageStatusDTOList;
    }
}
