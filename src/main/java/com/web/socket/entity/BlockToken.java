package com.web.socket.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("BlockToken")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlockToken {
    @Id
    private String uuid;
    @Indexed
    private String userKey;
}
