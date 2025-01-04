package com.web.socket.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;
    private String name;
    private String username;
    private String bio = "Hi there, I'm using Telegram";
    private String avatar = "https://res.cloudinary.com/dlanhtzbw/image/upload/v1675343188/Telegram%20Clone/no-profile_aknbeq.jpg";
    private List<Contact> contacts;
    private UserStatus status;
    private String password;
    private List<String> chatRooms;
    private List<String> pinnedChatRooms;
    private List<Object> unreadMessages;

    public static class UserStatus {
        private boolean online = true;
        private Date lastSeen;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getChatRooms() {
        return chatRooms;
    }

    public List<String> getPinnedChatRooms() {
        return pinnedChatRooms;
    }

    public List<Object> getUnreadMessages() {
        return unreadMessages;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setChatRooms(List<String> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public void setPinnedChatRooms(List<String> pinnedChatRooms) {
        this.pinnedChatRooms = pinnedChatRooms;
    }

    public void setUnreadMessages(List<Object> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
}
