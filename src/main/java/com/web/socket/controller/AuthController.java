package com.web.socket.controller;


import com.web.socket.dto.*;
import com.web.socket.dto.response.APIResponse;
import com.web.socket.dto.response.UserAuthResponse;
import com.web.socket.service.AuthService;
import com.web.socket.utils.APIResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
//    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody LoginRequest loginRequest) {
        UserAuthResponse userAuthResponse = authService.login(loginRequest);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_LOGIN.name())
                .data(userAuthResponse)
                .build();
        apiResponse.setData(userAuthResponse);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse> register(@RequestBody RegisterRequest registerRequest)  {
        authService.register(registerRequest);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_REGISTER.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/password")
    public ResponseEntity<APIResponse> changePassword(@RequestBody Map<String, String> params)  {
//        TokenResponse tokenResponse =  authService.changePassword(params.get("newPassword")
//                , params.get("oldPassword")
//                , Boolean.parseBoolean(params.get("isLogAllOut")));
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_UPDATED.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse> logout(@RequestHeader("Authorization") String token) {
//        token = jwtService.extractToken(token);
//        authService.logout(token);
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_LOGOUT.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<APIResponse> refreshToken(@RequestBody Map<String, String> params) {
//        TokenResponse tokenResponse = authService.refreshToken(params.get("refreshToken"));
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_CREATED.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
