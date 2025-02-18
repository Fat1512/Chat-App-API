package com.web.socket.entity.test;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

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

    private String subId;
}