package com.web.socket.dto;


import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebRTCMessage {
    private String type;
    private String sdp;
}
