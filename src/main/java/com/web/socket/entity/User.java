package com.web.socket.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document
@Getter
@Setter
@Builder
public class  User {
    @Id
    private String id;
    @Field
    private String name;
    @Field
    private String username;
    @Field
    private String email;
    @Field
    private String password;
    @Field
    private UserStatus status;

    @Builder.Default
    private String bio = "Hi there, I'm using Telegram";

    @Builder.Default
    private String avatar = "https://res.cloudinary.com/dlanhtzbw/image/upload/v1675343188/Telegram%20Clone/no-profile_aknbeq.jpg";

    @Builder.Default
    private List<UnreadMessage> unreadMessages = new ArrayList<>();

    @Builder.Default
    private List<UndeliveredMessage> undeliveredMessages = new ArrayList<>();

    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();

    @Builder.Default
    @DocumentReference(lazy = true)
    private Set<ChatRoom> chatRooms = new HashSet<>();

    @Builder.Default
    @DocumentReference(lazy = true)
    private List<ChatRoom> pinnedChatRooms = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStatus {
        private boolean online = true;
        private Double lastSeen;
    }
}
