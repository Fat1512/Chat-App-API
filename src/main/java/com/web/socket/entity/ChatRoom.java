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

//    @Builder.Default
//    @DocumentReference
//    private List<MessageHistory> messageHistory = new ArrayList<>();

    @Builder.Default
    @DocumentReference(lazy = true)
    private List<User> members = new ArrayList<>();

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        ChatRoom other = (ChatRoom) obj;
        return this.getId().equals(other.getId());
    }
}
