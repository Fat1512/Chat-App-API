package com.web.socket.service;

import com.web.socket.dto.ContactCreationRequest;
import com.web.socket.dto.ContactResponse;

import java.util.List;

public interface ContactService {
    List<ContactResponse> getContactList();
    ContactResponse createContact(ContactCreationRequest contactCreationRequest);
}
