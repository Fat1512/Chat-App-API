package com.web.socket.controller;

import com.web.socket.dto.*;
import com.web.socket.dto.request.MessageRequest;
import com.web.socket.dto.response.MessageResponse;
import com.web.socket.entity.User;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.ChatRoomService;
import com.web.socket.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
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
    public MessageResponse sendMessage(
            @DestinationVariable String chatRoomId,
            @RequestBody() MessageRequest messageRequest) {
        MessageResponse messageResponse = chatRoomService.pushMessageToChatRoom(messageRequest, chatRoomId);
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
        List<MessageStatusDTO> messageStatusDTOList = chatRoomService
                .markDeliveredMessages(chatRoomId);
        return messageStatusDTOList;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/callRequest")
    @SendTo("/topic/chatRoom/{chatRoomId}/callRequest")
    public WebRTCMessageDTO requestCall(
            @RequestBody WebRTCMessageDTO signalData) {
        return signalData;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/callAccepted")
    @SendTo("/topic/chatRoom/{chatRoomId}/callAccepted")
    public WebRTCMessageDTO acceptCall(
            @RequestBody WebRTCMessageDTO signalData) {
        return signalData;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/callEnded")
    @SendTo("/topic/chatRoom/{chatRoomId}/callEnded")
    public EndCallDTO sendEndCallRequest(@RequestBody EndCallDTO endCallDTO) {
        return endCallDTO;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/callDenied")
    @SendTo("/topic/chatRoom/{chatRoomId}/callDenied")
    public String sendDenyCallRequest() {
        return "Phat dep trai";
    }

    @MessageMapping("/login/{userId}/send")
    @SendTo("/topic/login/{userId}/send")
    public LoginEvent sendToken(@RequestBody LoginEvent loginEvent) {
        log.info("LoginEvent {}", loginEvent);
        return loginEvent;
    }
}
































































