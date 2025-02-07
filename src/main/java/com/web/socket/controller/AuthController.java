package com.web.socket.controller;


import com.web.socket.dto.LoginEvent;
import com.web.socket.dto.TokenDTO;
import com.web.socket.dto.response.APIResponse;
import com.web.socket.dto.request.LoginRequest;
import com.web.socket.dto.request.RegisterRequest;
import com.web.socket.dto.UserAuthDTO;
import com.web.socket.service.AuthService;
import com.web.socket.service.TokenService;
import com.web.socket.utils.APIResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody LoginRequest loginRequest) {
        UserAuthDTO userAuthDTO = authService.login(loginRequest);
        tokenService.deleteExceptCurrentToken(userAuthDTO.getId(), userAuthDTO.getTokenDTO().getUuid());

        //Logout all the previous logged-in session
        simpMessagingTemplate.convertAndSend(String.format("/topic/login/%s/send", userAuthDTO.getId()),
                LoginEvent.builder()
                        .authentication(String.format("Bearer %s", userAuthDTO.getTokenDTO().getAccessToken()))
                        .build());

        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_LOGIN.name())
                .data(userAuthDTO)
                .build();
        apiResponse.setData(userAuthDTO);
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
        TokenDTO tokenDTO =  authService.changePassword(params.get("newPassword")
                , params.get("oldPassword")
                , Boolean.parseBoolean(params.get("isLogAllOut")));
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_UPDATED.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse> logout() {
        authService.logout();

        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_LOGOUT.name())
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<APIResponse> refreshToken(@RequestBody Map<String, String> params) {
        TokenDTO tokenDTO = authService.refreshToken(params.get("refreshToken"));
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_CREATED.name())
                .data(tokenDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
