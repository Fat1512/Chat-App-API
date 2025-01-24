package com.web.socket.dto;


import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupCreationDTO {
    private String groupName;
    private List<String> membersId;
}
