package com.web.socket.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DocumentReference;


@Setter
@Getter
@Builder
public class Contact {
    @Builder.Default
    private String id = new ObjectId().toString();
    private String name;

    @DocumentReference(lazy = true)
    private User user;

    @DocumentReference(lazy = true)
    private ChatRoom chatRoom;
}
