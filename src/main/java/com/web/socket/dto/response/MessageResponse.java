package com.web.socket.dto.response;

import com.web.socket.entity.Message;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String id;
    private String messageType;
    private String content;
    private Double timeSent;
    private List<String> imageUrl;
    private Message.CallDetail callDetails;
    private Message.VoiceDetail voiceDetail;

    //2 beneath list support for group chat
    private List<String> undeliveredMembersId;
    private List<String> unreadMembersId;
    private Boolean readStatus;
    private Boolean deliveredStatus;
    private String senderId;
}
