package com.web.socket.service;

import com.web.socket.dto.request.LoginRequest;
import com.web.socket.dto.request.RegisterRequest;
import com.web.socket.dto.UserAuthDTO;

public interface AuthService {
    UserAuthDTO login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
    void logout();
    void test();
}
