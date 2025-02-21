package com.web.socket.service;

import com.web.socket.dto.UserProfileDTO;
import com.web.socket.dto.UserUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    UserProfileDTO getProfile(String userId);
    UserProfileDTO getProfile() ;
    Boolean isBelongToChatRoom(String chatRoomId);
    String uploadAvatar(MultipartFile multipartFile) throws IOException;
    UserUpdateDTO updateUser(UserUpdateDTO userUpdateDTO);
}
