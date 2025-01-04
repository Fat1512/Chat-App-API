package com.web.socket.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data
@Builder
public class User {
    @Id
    private String id;
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
    @DocumentReference
    private List<Contact> contacts = new ArrayList<>();

    @Builder.Default
    @DocumentReference
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @Builder.Default
    @DocumentReference
    private List<ChatRoom> pinnedChatRooms = new ArrayList<>();


    public static class UserStatus {
        private boolean online = true;
        private Date lastSeen;
    }

    @Data
    @Builder
    public static class UnreadMessage {
        @DocumentReference
        private Message message;
        @DocumentReference
        private ChatRoom chatRoom;
    }
}
