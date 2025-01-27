package com.web.socket.dto.request;


import com.web.socket.entity.Message;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String messageType;
    private String content;
    private List<String> imageUrl;
    private Message.CallDetail callDetails;
    private Message.VoiceDetail voiceDetail;
    private String senderId;
}
