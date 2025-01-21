package com.web.socket.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class GroupCreationRequest {
    private String groupName;
    private List<String> membersId;
}
