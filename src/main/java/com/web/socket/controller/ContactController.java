package com.web.socket.controller;

import com.web.socket.dto.ChatRoomSummaryDTO;
import com.web.socket.dto.LoginEvent;
import com.web.socket.dto.request.ContactCreationRequest;
import com.web.socket.dto.request.DeleteContactRequest;
import com.web.socket.dto.response.ContactResponse;
import com.web.socket.dto.response.APIResponse;
import com.web.socket.entity.RoomType;
import com.web.socket.service.ContactService;
import com.web.socket.utils.APIResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/contacts/delete-contact")
    public ResponseEntity<APIResponse> deleteContact(@RequestBody DeleteContactRequest deleteContactRequest) {
        contactService.deleteContact(deleteContactRequest);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/contacts")
    public ResponseEntity<APIResponse> getContacts() {
        List<ContactResponse> contactList = contactService.getContactList();
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(contactList)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/contacts/create-contact")
    public ResponseEntity<APIResponse> createContact(@RequestBody ContactCreationRequest contactCreationRequest) {
        ContactResponse contact = contactService.createContact(contactCreationRequest);

        simpMessagingTemplate.convertAndSend(String.format("/topic/chatRoom/%s/newChatRoom", contact.getRoomInfo().getId()),
                ChatRoomSummaryDTO
                        .builder()
                        .chatRoomId(contact.getChatRoomId())
                        .roomType(RoomType.PRIVATE)
                        .totalUnreadMessages(0)
                        .lastestMessage(null)
                        .roomInfo(contact.getRoomInfo())
                        .build());

        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_CREATED.name())
                .data(contact)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}























