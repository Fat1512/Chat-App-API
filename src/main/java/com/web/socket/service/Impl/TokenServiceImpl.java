package com.web.socket.service.Impl;

import com.web.socket.entity.BlockToken;
import com.web.socket.entity.Token;
import com.web.socket.repository.BlockTokenRedisRepository;
import com.web.socket.repository.TokenRedisRepository;
import com.web.socket.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRedisRepository tokenRedisRepository;
    private final BlockTokenRedisRepository blockTokenRedisRepository;

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
    public void deleteExceptCurrentToken(String userId, String uuid) {
        List<Token> tokens = this.findAllByUserKey(userId);
        Map<Boolean, List<Token>> partitionedTokens = tokens.stream()
                .collect(Collectors.partitioningBy(filterToken -> filterToken.getUuid().equals(uuid)));

        List<Token> remainingTokens = partitionedTokens.get(false);
        if(!remainingTokens.isEmpty()) {
            this.deleteAll(remainingTokens);
            this.addBlockTokens(remainingTokens);
        }
    }
}
