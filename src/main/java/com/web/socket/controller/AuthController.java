package com.web.socket.controller;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.web.socket.dto.LoginEvent;
import com.web.socket.dto.TokenDTO;
import com.web.socket.dto.request.OAuthAuthorizationRequest;
import com.web.socket.dto.request.OAuthLoginRequest;
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

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/oauth-url")
    public ResponseEntity<APIResponse> getGoogleOauthUrl() {
        String url = tokenService.getOauthUrl();
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_LOGIN.name())
                .data(url)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse> login(@RequestBody LoginRequest loginRequest) {
        UserAuthDTO userAuthDTO = authService.login(loginRequest);
//        tokenService.deleteAllExceptCurrentToken(userAuthDTO.getId(), userAuthDTO.getTokenDTO().getUuid());

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
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/oauth/login")
    public ResponseEntity<APIResponse> oauthLogin(@RequestBody OAuthAuthorizationRequest oauthAuthorizationRequest) throws IOException {
        Map<String, Object> response = tokenService.getOauthAccessToken(oauthAuthorizationRequest);


        JsonObject jsonObject = new Gson().toJsonTree(response).getAsJsonObject();
        UserAuthDTO userAuthDTO = authService.login(OAuthLoginRequest.builder()
                .name(authService.extractJsonValue(jsonObject, "names", "givenName"))
                .photo(authService.extractJsonValue(jsonObject, "photos", "url"))
                .email(authService.extractJsonValue(jsonObject, "emailAddresses", "value"))
                .build());

        tokenService.deleteAllExceptCurrentToken(userAuthDTO.getId(), userAuthDTO.getTokenDTO().getUuid());
        simpMessagingTemplate.convertAndSend(String.format("/topic/login/%s/send", userAuthDTO.getId()),
                LoginEvent.builder()
                        .authentication(String.format("Bearer %s", userAuthDTO.getTokenDTO().getAccessToken()))
                        .build());

        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_LOGIN.name())
                .data(userAuthDTO)
                .build();
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
                .data(tokenDTO)
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
        TokenDTO tokenDTO = tokenService.refreshToken(params.get("refreshToken"));
        APIResponse apiResponse = APIResponse.builder()
                .status(HttpStatus.OK)
                .message(APIResponseMessage.SUCCESSFULLY_CREATED.name())
                .data(tokenDTO)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
