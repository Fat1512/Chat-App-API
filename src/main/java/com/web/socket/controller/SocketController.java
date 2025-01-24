package com.web.socket.controller;

import com.web.socket.dto.MessageDTO;
import com.web.socket.dto.MessageEventDTO;
import com.web.socket.dto.MessageStatusDTO;
import com.web.socket.dto.WebRTCMessage;
import com.web.socket.entity.User;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.ChatRoomService;
import com.web.socket.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocketController {

    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    @MessageMapping("/test")
    public void test() {
        log.info("Header {}", "disconnected");
    }

    @MessageMapping("/chatRoom/{chatRoomId}/sendMessage")
    @SendTo("/topic/chatRoom/{chatRoomId}/newMessages")
    public MessageDTO sendMessage(
            @DestinationVariable String chatRoomId,
            @RequestBody MessageDTO messageDTO) {
        MessageDTO messageResponse = chatRoomService.pushMessageToChatRoom(messageDTO, chatRoomId);
        return messageResponse;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/typing")
    @SendTo("/topic/chatRoom/{chatRoomId}/typing")
    public MessageEventDTO typeMessage() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        MessageEventDTO messageEventDTO =
                MessageEventDTO.builder()
                        .senderId(authenticatedUser.getId())
                        .mode("Typing...").build();
        return messageEventDTO;
    }

    @MessageMapping("/disconnect")
    public void disconnect() {
        chatRoomService.broadcastOfflineStatus();
    }

    @MessageMapping("/connect")
    public void connect() {
        chatRoomService.broadcastOnlineStatus();
    }

    @MessageMapping("/chatRoom/{chatRoomId}/markAsRead")
    @SendTo("/topic/chatRoom/{chatRoomId}/message/readStatus")
    public List<MessageStatusDTO> markAsReadMessage(
            @DestinationVariable String chatRoomId) {
        List<MessageStatusDTO> messageStatusDTOList = chatRoomService
                .markReadMessages(chatRoomId);
        return messageStatusDTOList;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/markAsDelivered")
    @SendTo("/topic/chatRoom/{chatRoomId}/message/deliveredStatus")
    public List<MessageStatusDTO> markAsDeliveredMessage(
            @DestinationVariable String chatRoomId) {
        List<MessageStatusDTO> messageStatusDTOList = chatRoomService.markDeliveredMessages(chatRoomId);
        return messageStatusDTOList;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/callRequest")
    @SendTo("/topic/chatRoom/{chatRoomId}/callRequest")
    public WebRTCMessage requestCall(
            @RequestBody WebRTCMessage signalData) {
        return signalData;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/callAccepted")
    @SendTo("/topic/chatRoom/{chatRoomId}/callAccepted")
    public WebRTCMessage acceptCall(
            @RequestBody WebRTCMessage signalData) {
        return signalData;
    }


}
































































