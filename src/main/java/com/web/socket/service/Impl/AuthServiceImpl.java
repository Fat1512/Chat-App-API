package com.web.socket.service.Impl;


import com.web.socket.dto.request.LoginRequest;
import com.web.socket.dto.request.RegisterRequest;
import com.web.socket.dto.TokenDTO;
import com.web.socket.dto.UserAuthDTO;
import com.web.socket.entity.BlockToken;
import com.web.socket.entity.Token;
import com.web.socket.entity.User;
import com.web.socket.exception.InvalidJwtTokenException;
import com.web.socket.exception.OverlapResourceException;
import com.web.socket.repository.BlockTokenRedisRepository;
import com.web.socket.repository.TokenRedisRepository;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.AuthService;
import com.web.socket.service.TokenService;
import com.web.socket.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();


        if(username == null || password == null)
            throw new BadCredentialsException("Wrong username or password");

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(userDetail.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username doesn't exist"));

        TokenDTO tokenDTO = jwtService.generateToken(userDetail, user.getId());
        Token redisToken = Token.builder()
                .uuid(tokenDTO.getUuid())
                .userKey(user.getId())
                .timeToLive(expirationTime)
                .build();

        tokenRedisRepository.save(redisToken);
        return UserAuthDTO.builder()
                .id(user.getId())
                .onlineStatus(true)
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
        String password = registerRequest.getPassword();
        String confirmedPassword = registerRequest.getConfirmedPassword();

        if(name == null || username == null || password == null || confirmedPassword == null)
            throw new BadCredentialsException("Fields must not be null");
        if(!password.equals(confirmedPassword))
            throw new BadCredentialsException("Password doesn't match each other");

        User user = userRepository.findByUsername(username).orElse(null);

        if(user != null) {
            throw new BadCredentialsException("Username existed !");
        }

        user = User.builder()
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

    @Override
    @Transactional
    public TokenDTO refreshToken(String refreshToken) {

        String uuid = jwtService.extractUuid(refreshToken);
        if(uuid == null)
            throw new InvalidJwtTokenException("Bad refresh token !");

        if (!jwtService.validateToken(refreshToken))
            throw new InvalidJwtTokenException("Refresh token invalid or expired");



        Optional<Token> token = tokenRedisRepository.findById(uuid);
        if (token.isPresent())
            throw new OverlapResourceException("Access key is still valid !");

        Optional<BlockToken> blockToken = blockTokenRedisRepository.findById(uuid);
        if(blockToken.isPresent())
            throw new InvalidJwtTokenException("Access token cannot be requested");



        User user = userRepository.findByUsername(jwtService.extractUsername(refreshToken))
                .orElseThrow(() -> new InvalidJwtTokenException("Bad JWT credentials info"));

        TokenDTO tokenResponse = jwtService
                .generateToken(new org.springframework.security.core.userdetails.
                                User(user.getUsername(), user.getPassword(), new ArrayList<>()), user.getId());

        token = Optional.ofNullable(Token.builder()
                .uuid(tokenResponse.getUuid())
                .userKey(user.getId())
                .timeToLive(expirationTime)
                .build());

        tokenRedisRepository.save(token.get());
        tokenRedisRepository.deleteById(uuid);
        return tokenResponse;
    }
}













































