package com.web.socket.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.web.socket.dto.request.LoginRequest;
import com.web.socket.dto.request.OAuthAuthorizationRequest;
import com.web.socket.dto.request.OAuthLoginRequest;
import com.web.socket.dto.request.RegisterRequest;
import com.web.socket.dto.TokenDTO;
import com.web.socket.dto.UserAuthDTO;
import com.web.socket.entity.BlockToken;
import com.web.socket.entity.Token;
import com.web.socket.entity.User;
import com.web.socket.exception.BadRequestException;
import com.web.socket.exception.InvalidJwtTokenException;
import com.web.socket.repository.BlockTokenRedisRepository;
import com.web.socket.repository.TokenRedisRepository;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.AuthService;
import com.web.socket.utils.SecurityUtils;
import com.web.socket.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final TokenRedisRepository tokenRedisRepository;
    private final BlockTokenRedisRepository blockTokenRedisRepository;

    private final JwtService jwtService;

    @Value(value = "${app.token.expirationTime}")
    private int expirationTime;

    @Override
    @Transactional
    public UserAuthDTO login(LoginRequest loginRequest) {
        String usernameOrEmail = loginRequest.getUsernameOrEmail();
        String password = loginRequest.getPassword();

        if(usernameOrEmail == null || password == null)
            throw new BadCredentialsException("Wrong username/email or password");

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsernameOrEmail(userDetail.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username/Email doesn't exist"));

        TokenDTO tokenDTO = jwtService.generateToken(userDetail.getUsername(), user.getId());
        Token redisToken = Token.builder()
                .uuid(tokenDTO.getUuid())
                .userKey(user.getId())
                .timeToLive(expirationTime)
                .build();

        tokenRedisRepository.save(redisToken);
        return UserAuthDTO.builder()
                .id(user.getId())
                .onlineStatus(user.getStatus().isOnline())
                .name(user.getName())
                .username(user.getUsername())
                .bio(user.getBio())
                .avt(user.getAvatar())
                .isAuthenticated(true)
                .tokenDTO(tokenDTO)
                .build();
    }

    /**
     *
     * check email
     * exist = return
     * <> = create & random username
     *
     */
    @Override
    @Transactional
    public UserAuthDTO login(OAuthLoginRequest loginRequest) {

        Optional<User> checkingUser = userRepository.findByEmail(loginRequest.getEmail());
        User user;
        if(checkingUser.isEmpty()) {
            String randomUserSalt = UUID.randomUUID().toString().substring(0, 7);
            user = User.builder()
                    .name(loginRequest.getName())
                    .username(String.format("%s-%s", loginRequest.getEmail().split("@")[0], randomUserSalt))
                    .password(passwordEncoder.encode(randomUserSalt))
                    .email(loginRequest.getEmail())
                    .avatar(loginRequest.getPhoto())
                    .status(new User.UserStatus())
                    .build();
            userRepository.save(user);
        } else
            user = checkingUser.get();

        TokenDTO tokenDTO = jwtService.generateToken(user.getUsername(), user.getId());
        Token redisToken = Token.builder()
                .uuid(tokenDTO.getUuid())
                .userKey(user.getId())
                .timeToLive(expirationTime)
                .build();
        tokenRedisRepository.save(redisToken);
        return UserAuthDTO.builder()
                .id(user.getId())
                .onlineStatus(user.getStatus().isOnline())
                .name(user.getName())
                .username(user.getUsername())
                .bio(user.getBio())
                .avt(user.getAvatar())
                .isAuthenticated(true)
                .tokenDTO(tokenDTO)
                .build();
    }

    @Override
    public TokenDTO changePassword(String newPassword, String oldPassword, Boolean isLogAllOut) {
        return null;
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        String name = registerRequest.getName();
        String username = registerRequest.getUsername();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String confirmedPassword = registerRequest.getConfirmedPassword();

        if(name == null || username == null || password == null || confirmedPassword == null|| email == null)
            throw new BadCredentialsException("Fields must not be null");
        if(!password.equals(confirmedPassword))
            throw new BadCredentialsException("Password doesn't match each other");
        if(!ValidationUtils.isValidEmail(email))
            throw new BadCredentialsException("Invalid email format");

        boolean isEmailExisted = userRepository.existsByEmail(email);
        if(isEmailExisted)
            throw new BadCredentialsException("Email existed !");

        Optional<User> checkingUser = userRepository.findByUsername(username);
        if(checkingUser.isPresent()) {
            throw new BadCredentialsException("Username existed !");
        }

        User user = User.builder()
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(password))
                .status(new User.UserStatus())
                .build();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void logout() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        log.info("logout: {}", authenticatedUser.getId());
        List<Token> tokens = tokenRedisRepository.findAllByUserKey(authenticatedUser.getId());
        tokenRedisRepository.deleteAllById(tokens.stream().map(Token::getUuid).toList());
        blockTokenRedisRepository.saveAll(tokens.stream()
                .map(token -> BlockToken.builder()
                        .uuid(token.getUuid())
                        .userKey(token.getUserKey())
                        .build()).toList());
    }

    public String extractJsonValue(JsonObject jsonObject, String arrayName, String field) {
        JsonArray jsonArray = jsonObject.getAsJsonArray(arrayName);
        if(jsonArray != null && !jsonArray.isEmpty()) {
            return jsonArray.get(0).getAsJsonObject().get(field).getAsString();
        }
        return null;
    }
}










































