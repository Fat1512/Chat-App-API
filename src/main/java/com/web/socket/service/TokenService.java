package com.web.socket.service;

import com.web.socket.dto.TokenDTO;
import com.web.socket.dto.request.OAuthAuthorizationRequest;
import com.web.socket.entity.Token;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface TokenService {
    void save(Token token);
    void delete(String uuid);
    void deleteAll(List<Token> tokens);
    Token get(String uuid);
    List<Token> findAllByUserKey(String userKey);
    void invalidateAllUserToken(String userId);
    void deleteAllExceptCurrentToken(String userId, String uuid);
    void addBlockTokens(List<Token> tokens);
    TokenDTO refreshToken(String refreshToken);

    String getOauthUrl();
    Map<String, Object> getOauthAccessToken(OAuthAuthorizationRequest oauthAuthorizationRequest) throws IOException;
}
