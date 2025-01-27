package com.web.socket.service;

import com.web.socket.dto.request.ContactCreationRequest;
import com.web.socket.dto.response.ContactResponse;

import java.util.List;

public interface ContactService {
    List<ContactResponse> getContactList();
    ContactResponse createContact(ContactCreationRequest contactCreationRequest);
}
