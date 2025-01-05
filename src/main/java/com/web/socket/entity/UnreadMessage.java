package com.web.socket.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Setter
@Getter
@Builder
public class UnreadMessage {
    @DocumentReference(lazy = false)
    private Message message;
    @DocumentReference(lazy = false)
    private ChatRoom chatRoom;
}