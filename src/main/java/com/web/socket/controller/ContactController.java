package com.web.socket.controller;

import com.web.socket.dto.ContactCreationRequest;
import com.web.socket.dto.ContactResponse;
import com.web.socket.dto.response.APIResponse;
import com.web.socket.service.ContactService;
import com.web.socket.utils.APIResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/contacts")
    public ResponseEntity<APIResponse> getContacts()  {
        List<ContactResponse> contactList = contactService.getContactList();
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(contactList)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/contacts/create-contact")
    public ResponseEntity<APIResponse> createContact(@RequestBody ContactCreationRequest contactCreationRequest)  {
        ContactResponse contact = contactService.createContact(contactCreationRequest);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_RETRIEVED.name())
                .data(contact)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}























