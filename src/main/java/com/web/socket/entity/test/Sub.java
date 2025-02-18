package com.web.socket.entity.test;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection="Sub")
@AllArgsConstructor
@NoArgsConstructor
public class Sub {

    @Id
    private String id;
    private String name;
}
