package com.web.socket.service.Impl;

import com.web.socket.entity.Token;
import com.web.socket.repository.TokenRedisRepository;
import com.web.socket.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRedisRepository tokenRedisRepository;

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
}
