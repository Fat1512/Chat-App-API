package com.web.socket.service;

import com.google.gson.JsonObject;
import com.web.socket.dto.TokenDTO;
import com.web.socket.dto.request.LoginRequest;
import com.web.socket.dto.request.OAuthAuthorizationRequest;
import com.web.socket.dto.request.OAuthLoginRequest;
import com.web.socket.dto.request.RegisterRequest;
import com.web.socket.dto.UserAuthDTO;

import java.io.IOException;
import java.util.Map;

public interface AuthService {
    UserAuthDTO login(LoginRequest loginRequest);
    UserAuthDTO login(OAuthLoginRequest loginRequest);
    TokenDTO changePassword(String newPassword, String oldPassword, Boolean isLogAllOut);
    void register(RegisterRequest registerRequest);
    void logout();
    String extractJsonValue(JsonObject jsonObject, String arrayName, String field);
}
