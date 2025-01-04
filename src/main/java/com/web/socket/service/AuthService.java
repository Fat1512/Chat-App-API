package com.web.socket.service;

import com.web.socket.dto.LoginRequest;
import com.web.socket.dto.RegisterRequest;
import com.web.socket.dto.UserAuthResponse;

public interface AuthService {
    UserAuthResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
}
