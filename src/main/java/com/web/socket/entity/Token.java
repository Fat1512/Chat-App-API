package com.web.socket.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("Token")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token implements Serializable {
    @Id
    private String uuid;
    @Indexed
    private String userKey;
    @TimeToLive
    private Integer timeToLive;
}
