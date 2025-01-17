package com.web.socket.service;

import com.web.socket.dto.OnlineStatusDTO;
import com.web.socket.dto.UserProfileDTO;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface UserService {
    UserProfileDTO getProfile(String userId);
    UserProfileDTO getProfile() ;
    OnlineStatusDTO switchToOnlineStatus() ;
    OnlineStatusDTO switchToOfflineStatus() ;
}
