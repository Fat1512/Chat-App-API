package com.web.socket.entity;


import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
@Builder
public class ChatRoom {
    @Id
    private String Id;
    private RoomType roomType;

    @Builder.Default
    private List<MessageHistory> messageHistory = new ArrayList<>();

    @Builder.Default
    @DocumentReference
    private List<User> members = new ArrayList<>();

    public enum RoomType {
        PRIVATE, GROUP
    }

    @Data
    @Builder
    public static class MessageHistory {
        @Id
        @Builder.Default
        private String Id = new ObjectId().toString();
        private String day;
        private List<Message> messages;
    }
}