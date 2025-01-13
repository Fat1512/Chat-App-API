package com.web.socket.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection="Parent")
@AllArgsConstructor
@NoArgsConstructor
public class Parent {
    @Id
    private String id;
    private String userName;
}