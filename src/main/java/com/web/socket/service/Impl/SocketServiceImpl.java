package com.web.socket.service.Impl;


import com.web.socket.dto.ChatRoomSummaryDTO;
import com.web.socket.entity.RoomType;
import com.web.socket.service.SocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocketServiceImpl implements SocketService {
    private final SimpMessagingTemplate simpMessagingTemplate;

//    @Override
//    public void publishNewChatRoom(String userId) {
//        simpMessagingTemplate.convertAndSend(String.format("/topic/chatRoom/%s/newChatRoom", contact.getRoomInfo().getId()),
//                ChatRoomSummaryDTO
//                        .builder()
//                        .chatRoomId(contact.getChatRoomId())
//                        .roomType(RoomType.PRIVATE)
//                        .totalUnreadMessages(0)
//                        .lastestMessage(null)
//                        .roomInfo(contact.getRoomInfo())
//                        .build());
//    }
}
