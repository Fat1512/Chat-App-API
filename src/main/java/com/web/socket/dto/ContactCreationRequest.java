package com.web.socket.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class ContactCreationRequest {
    private String username, name;
}
