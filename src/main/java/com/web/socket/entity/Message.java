package com.web.socket.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Builder
public class Message {
    @Id
    @Builder.Default
    private String id = new ObjectId().toString();
    private String messageType;
    private boolean readStatus;
    private boolean deliveredStatus;
    private String content;
    private Date timeSent;
    private String imageUrl;
    private CallDetail callDetails;
    private VoiceDetail voiceDetail;

    @Builder.Default
    @DocumentReference
    private List<User> undeliveredMembers = new ArrayList<>();

    @Builder.Default
    @DocumentReference
    private List<User> unreadMembers = new ArrayList<>();

    @DocumentReference
    private User sender;


    @Data
    @Builder
    public static class VoiceDetail {
        @Id
        @Builder.Default
        private String id = new ObjectId().toString();
        private String voiceNoteUrl;
        private String voiceNoteDuration;
    }

    @Data
    @Builder
    public static class CallDetail {
        @Id
        @Builder.Default
        private String id = new ObjectId().toString();
        private String callType;
        private String callDuration;
        private CallRejectReason callRejectReason;

        public enum CallRejectReason {
            MISSED, BUSY
        }
    }
}