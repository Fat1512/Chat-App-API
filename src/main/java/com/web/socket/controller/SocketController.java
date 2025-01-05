package com.web.socket.controller;
import com.web.socket.dto.response.APIResponse;
import com.web.socket.dto.response.MessageDTO;
import com.web.socket.service.ChatRoomService;
import com.web.socket.utils.APIResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocketController {

    private final ChatRoomService chatRoomService;

    @MessageMapping("/chatRoom/{chatRoomId}/sendMessage")
    public MessageDTO sendMessage(
            @DestinationVariable String chatRoomId,
            @Payload MessageDTO messageDTO) {
        MessageDTO messageResponse = chatRoomService.pushMessageToChatRoom(messageDTO, chatRoomId);
        return messageResponse;
    }

    @MessageMapping("/chatRoom/{chatRoomId}/readMessage")
    public ResponseEntity<APIResponse> readMessage(@DestinationVariable String chatRoomId) {
//        chatRoomService.pushMessageToChatRoom(messageDTO, chatRoomId);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_DELETED.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
