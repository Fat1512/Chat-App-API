package com.web.socket.entity;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

public class Contact {
    @MongoId
    private String id;
    private String name;
    private String chatRoomId;

    public Contact() {
        this.id = new ObjectId(new Date()).toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}