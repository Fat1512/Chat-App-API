package com.web.socket.dto.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class DeleteContactRequest {
    public String contactId;
}
