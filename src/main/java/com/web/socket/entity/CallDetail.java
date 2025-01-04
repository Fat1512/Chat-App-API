package com.web.socket.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CallDetail {
    private String callType;
    private String callDuration;
    private CallRejectReason callRejectReason;

    public enum CallRejectReason {
        MISSED, BUSY
    }
}