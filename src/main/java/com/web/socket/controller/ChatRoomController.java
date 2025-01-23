package com.web.socket.controller;


import com.web.socket.dto.*;
import com.web.socket.dto.response.APIResponse;
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
    @PostMapping("/chatrooms/create-chatroom")
    public ResponseEntity<APIResponse> createChatRoom(@RequestBody GroupCreationDTO groupCreationDTO)  {
        chatRoomService.createGroup(groupCreationDTO);
//        APIResponse apiResponse = APIResponse.builder()
//                .status(HttpStatus.OK)
//                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
//                .data(chatRoomSummaryList)
//                .build();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    @GetMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<APIResponse> getChatRoomDetail(@PathVariable String chatRoomId) {
        ChatRoomDetailDTO chatRoomDetailDTO = chatRoomService.getChatRoomDetail(chatRoomId);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(chatRoomDetailDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/chatrooms/{chatRoomId}")
    public ResponseEntity<APIResponse> pushMessageToChatRoom(@RequestBody MessageDTO messageDTO, @PathVariable String chatRoomId)  {
        MessageDTO messageResponse = chatRoomService.pushMessageToChatRoom(messageDTO, chatRoomId);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_UPDATED.name())
                .data(messageResponse)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/chatrooms/{chatRoomId}/markAsRead")
    public ResponseEntity<APIResponse> markReadMessages(@PathVariable String chatRoomId)  {
        List<MessageStatusDTO> messageStatusDTOList = chatRoomService.markReadMessages(chatRoomId);

        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_UPDATED.name())
                .data(messageStatusDTOList)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/chatrooms/{chatRoomId}/markAsDelivered")
    public ResponseEntity<APIResponse> markDeliveredMessages(@PathVariable String chatRoomId)  {
        List<MessageStatusDTO> messageStatusDTOList = chatRoomService.markDeliveredMessages(chatRoomId);

        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_UPDATED.name())
                .data(messageStatusDTOList)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
