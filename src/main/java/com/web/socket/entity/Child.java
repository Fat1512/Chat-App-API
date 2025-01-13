package com.web.socket.entity;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection="Parent")
@AllArgsConstructor
@NoArgsConstructor
public class Child extends Parent {
    private String groupName;
}
