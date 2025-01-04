package com.web.socket.entity;

import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

public class Message {
    @Id
    private String id;
    private String messageType;
    private String senderId;
    private boolean readStatus = false;
    private boolean deliveredStatus = false;
    private List<String> undeliveredMembers;
    private List<String> unreadMembers;
    private Date timeSent;
    private String message;
    private String imageUrl;
    private CallDetail callDetails;
    private VoiceDetail voiceDetail;
}