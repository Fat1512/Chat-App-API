package com.web.socket.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;

public class VoiceDetail {
    @Id
    private String id;
    private String voiceNoteUrl;
    private String voiceNoteDuration;

    public VoiceDetail() {
        this.id = new ObjectId(new Date()).toString();
    }

    public void setVoiceNoteUrl(String voiceNoteUrl) {
        this.voiceNoteUrl = voiceNoteUrl;
    }

    public void setVoiceNoteDuration(String voiceNoteDuration) {
        this.voiceNoteDuration = voiceNoteDuration;
    }
}