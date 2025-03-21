package com.web.socket.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @Builder.Default
    private String id = new ObjectId().toString();
    private String messageType;
    private String content;
    private Double timeSent;
    private List<String> imageUrl;
    private CallDetail callDetails;
    private VoiceDetail voiceDetail;

    @Builder.Default
    @DocumentReference(lazy = true)
    private List<User> undeliveredMembers = new ArrayList<>();

    @Builder.Default
    @DocumentReference(lazy = true)
    private List<User> unreadMembers = new ArrayList<>();

    @DocumentReference(lazy = true)
    private User sender;


    @Setter @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VoiceDetail {
        @Builder.Default
        private String id = new ObjectId().toString();
        private String voiceNoteUrl;
        private String voiceNoteDuration;
    }

    @Setter @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CallDetail {
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