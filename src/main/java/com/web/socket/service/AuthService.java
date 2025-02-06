package com.web.socket.service;

import com.web.socket.dto.TokenDTO;
import com.web.socket.dto.request.LoginRequest;
import com.web.socket.dto.request.RegisterRequest;
import com.web.socket.dto.UserAuthDTO;

public interface AuthService {
    UserAuthDTO login(LoginRequest loginRequest);
    TokenDTO changePassword(String newPassword, String oldPassword, Boolean isLogAllOut);
    void register(RegisterRequest registerRequest);
    void logout();
    TokenDTO refreshToken(String refreshToken);
}
