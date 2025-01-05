package com.web.socket.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MessageStatusRequest {
    private List<String> messagesId;
}
