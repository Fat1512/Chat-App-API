package com.web.socket.dto;

import com.web.socket.entity.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class MessageDTO {
    private String id;
    private String messageType;
    private String content;
    private Double timeSent;
    private String imageUrl;
    private Message.CallDetail callDetails;
    private Message.VoiceDetail voiceDetail;

    private List<String> undeliveredMembersId;
    private List<String> unreadMembersId;
    private String senderId;
}
