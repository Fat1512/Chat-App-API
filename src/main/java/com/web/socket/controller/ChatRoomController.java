package com.web.socket.controller;


import com.web.socket.dto.response.APIResponse;
import com.web.socket.dto.response.ChatRoomSummaryDTO;
import com.web.socket.dto.response.MessageDTO;
import com.web.socket.dto.response.MessageStatusRequest;
import com.web.socket.service.ChatRoomService;
import com.web.socket.utils.APIResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/chatrooms")
    public ResponseEntity<APIResponse> getChatRoomSummary() {
        List<ChatRoomSummaryDTO> chatRoomSummaryList = chatRoomService.getChatRoomSummary();
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(chatRoomSummaryList)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<APIResponse> pushMessageToChatRoom(@RequestBody MessageDTO messageDTO, @PathVariable String chatRoomId) {
        chatRoomService.pushMessageToChatRoom(messageDTO, chatRoomId);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_DELETED.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/chatrooms/{chatRoomId}/markAsRead")
    public ResponseEntity<APIResponse> mark(@RequestBody MessageStatusRequest messageStatusRequest, @PathVariable String chatRoomId) {
        chatRoomService.markReadMessage(messageStatusRequest.getMessagesId(), chatRoomId);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_DELETED.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
