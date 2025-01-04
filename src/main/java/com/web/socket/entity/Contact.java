package com.web.socket.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;


@Data
@Builder
public class Contact {
    @Id
    @Builder.Default
    private String id = new ObjectId().toString();
    private String name;

    @DocumentReference
    private ChatRoom chatRoom;
}