package com.web.socket.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class EndCallDTO {
    private String senderId; //user id
    private String duration;
}
