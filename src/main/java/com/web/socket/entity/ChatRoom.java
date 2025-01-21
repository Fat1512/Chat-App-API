package com.web.socket.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Document
public class ChatRoom {
    @Id
    private String id;

    private RoomType roomType;

    private String groupName;
    private String groupAvatar;
    @Builder.Default
    private List<MessageHistory> messageHistory = new ArrayList<>();

    @Builder.Default
    @DocumentReference(lazy = true)
    private List<User> members = new ArrayList<>();

    @Setter @Getter
    @Builder
    public static class MessageHistory {
        @Id
        @Builder.Default
        private String Id = new ObjectId().toString();
        private Double day;
        private List<Message> messages;
    }
}