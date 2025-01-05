package com.web.socket.dto.response;

import lombok.*;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
//    @JsonIgnore
//    private String uuid;
}
