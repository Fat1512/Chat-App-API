package com.web.socket.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.web.socket.dto.TokenDTO;
import com.web.socket.dto.request.OAuthAuthorizationRequest;
import com.web.socket.entity.BlockToken;
import com.web.socket.entity.Token;
import com.web.socket.entity.User;
import com.web.socket.exception.BadRequestException;
import com.web.socket.exception.InvalidJwtTokenException;
import com.web.socket.repository.BlockTokenRedisRepository;
import com.web.socket.repository.TokenRedisRepository;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final BlockTokenRedisRepository blockTokenRedisRepository;

    @Value(value = "${app.token.expirationTime}")
    private int expirationTime;

    @Value(value = "${spring.security.oauth2.client.registration.google.prefix-uri}")
    private String prefixUri;

    @Value(value = "${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value(value = "${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value(value = "${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Value(value = "${spring.security.oauth2.client.registration.google.scope}")
    private String scope;

    @Value(value = "${spring.security.oauth2.client.registration.google.user-info-uri}")
    private String userInfoUri;

    @Override
    @Transactional
    public void save(Token token) {
        tokenRedisRepository.save(token);
    }

    @Override
    @Transactional
    public void delete(String uuid) {
        tokenRedisRepository.deleteById(uuid);
    }

    @Override
    @Transactional
    public void deleteAll(List<Token> tokens) {
        tokenRedisRepository.deleteAll(tokens);
    }

    @Override
    @Transactional
    public void addBlockTokens(List<Token> tokens) {
        blockTokenRedisRepository.saveAll(tokens.stream()
                .map(token -> BlockToken
                        .builder()
                        .uuid(token.getUuid())
                        .userKey(token.getUserKey())
                        .build()).toList());
    }

    @Override
    public Token get(String uuid) {
        return tokenRedisRepository.findById(uuid).orElse(null);
    }

    @Override
    public List<Token> findAllByUserKey(String userKey) {
        return tokenRedisRepository.findAllByUserKey(userKey);
    }

    @Override
    @Transactional
    public void invalidateAllUserToken(String userId) {
        List<Token> tokens = tokenRedisRepository.findAllByUserKey(userId);
        tokenRedisRepository.deleteAllById(tokens.stream().map(Token::getUuid).toList());
    }

    @Override
    @Transactional
    public void deleteAllExceptCurrentToken(String userId, String uuid) {
        List<Token> tokens = this.findAllByUserKey(userId);
        Map<Boolean, List<Token>> partitionedTokens = tokens.stream()
                .collect(Collectors.partitioningBy(filterToken -> filterToken.getUuid().equals(uuid)));

        List<Token> remainingTokens = partitionedTokens.get(false);
        if(!remainingTokens.isEmpty()) {
            this.deleteAll(remainingTokens);
            this.addBlockTokens(remainingTokens);
        }
    }

    @Override
    @Transactional
    public TokenDTO refreshToken(String refreshToken) {

        String uuid = jwtService.extractUuid(refreshToken);
        String userId = jwtService.extractUserId(refreshToken);
        if(uuid == null)
            throw new InvalidJwtTokenException("Bad refresh token !");

        if (!jwtService.validateToken(refreshToken))
            throw new InvalidJwtTokenException("Refresh token invalid or expired");



        Optional<Token> token = tokenRedisRepository.findById(uuid);
        if (token.isPresent())
            throw new InvalidJwtTokenException("Access key is still valid !");

        Optional<BlockToken> blockToken = blockTokenRedisRepository.findById(uuid);
        if(blockToken.isPresent())
            throw new InvalidJwtTokenException("Access token cannot be requested");

        if(!tokenRedisRepository.findAllByUserKey((userId)).isEmpty()) {
            throw new BadRequestException("User already has key before !");
        }

        User user = userRepository.findByUsername(jwtService.extractUsername(refreshToken))
                .orElseThrow(() -> new InvalidJwtTokenException("Bad JWT credentials info"));

        TokenDTO tokenResponse = jwtService
                .generateToken(user.getUsername(), user.getId());

        token = Optional.ofNullable(Token.builder()
                .uuid(tokenResponse.getUuid())
                .userKey(user.getId())
                .timeToLive(expirationTime)
                .build());
        log.info("refresh token: {}", uuid);
        tokenRedisRepository.save(token.get());
        tokenRedisRepository.deleteById(uuid);
        return tokenResponse;
    }

    @Override
    @Transactional
    public Map<String, Object> getOauthAccessToken(OAuthAuthorizationRequest oauthAuthorizationRequest) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        String accessToken = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                clientId,
                clientSecret,
                oauthAuthorizationRequest.getAuthorizationCode(),
                redirectUri).execute().getAccessToken();

        restTemplate.getInterceptors().add((req, body, executionContext) -> {
            req.getHeaders().set("Authorization", String.format("Bearer %s", accessToken));
            return executionContext.execute(req, body);
        });

        return new ObjectMapper().readValue(
                restTemplate.getForEntity(userInfoUri, String.class).getBody(),
                new TypeReference<>() {});
    }


    @Override
    public String getOauthUrl() {
        return String.format("%s?client_id=%s&redirect_uri=%s&scope=%s&response_type=code&prompt=consent&access_type=offline&include_granted_scopes=true&state=abcxyz123", prefixUri, clientId, redirectUri, scope);
    }

}
