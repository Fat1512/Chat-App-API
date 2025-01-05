package com.web.socket.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Getter
@Setter
@Builder
public class User {
    @Id
    private ObjectId id;
    private String name;
    private String username;
    private String password;
    private UserStatus status;

    @Builder.Default
    private String bio = "Hi there, I'm using Telegram";

    @Builder.Default
    private String avatar = "https://res.cloudinary.com/dlanhtzbw/image/upload/v1675343188/Telegram%20Clone/no-profile_aknbeq.jpg";

    @Builder.Default
    private List<UnreadMessage> unreadMessages = new ArrayList<>();

    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();

    @Builder.Default
    @DocumentReference(lazy = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @Builder.Default
    @DocumentReference(lazy = true)
    private List<ChatRoom> pinnedChatRooms = new ArrayList<>();

    @Getter
    public static class UserStatus {
        private boolean online = true;
        private Date lastSeen;
    }
}
