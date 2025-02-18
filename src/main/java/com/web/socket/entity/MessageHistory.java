package com.web.socket.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Setter
@Getter
@Builder
@Document
public class MessageHistory {
    @Id
    private String Id;
    private Double day;
    @Field(targetType = FieldType.OBJECT_ID)
    private String chatRoomId;
    private List<Message> messages;
}
