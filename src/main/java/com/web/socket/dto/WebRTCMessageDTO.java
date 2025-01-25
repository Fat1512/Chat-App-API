package com.web.socket.dto;


import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebRTCMessageDTO {

    private String callerId;
    private String callerName;
    private SignalDTO rtcSignal;

    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignalDTO {
        private String type;
        private String sdp;
    }
}
