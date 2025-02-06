package com.web.socket.service;

import com.web.socket.entity.Token;

import java.util.List;

public interface TokenService {
    void save(Token token);
    void delete(String uuid);
    void deleteAll(List<Token> tokens);
    Token get(String uuid);
    List<Token> findAllByUserKey(String userKey);
    void invalidateAllUserToken(String userId);
    void deleteExceptCurrentToken(String userId, String uuid);
    void addBlockTokens(List<Token> tokens);
}
