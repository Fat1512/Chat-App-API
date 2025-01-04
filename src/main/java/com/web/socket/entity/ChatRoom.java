package com.web.socket.entity;


import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class ChatRoom {
    private RoomType roomType;
    private List<String> members;
    private List<MessageHistory> messageHistory;

    public enum RoomType {
        PRIVATE, GROUP
    }

    public static class MessageHistory {
        private int day;
        private List<Message> messages;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void setMessageHistory(List<MessageHistory> messageHistory) {
        this.messageHistory = messageHistory;
    }
}