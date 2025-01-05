package com.web.socket.service;

import com.web.socket.dto.UserProfileDTO;

public interface UserService {
    UserProfileDTO getProfile(String userId);
    UserProfileDTO getProfile();
}
