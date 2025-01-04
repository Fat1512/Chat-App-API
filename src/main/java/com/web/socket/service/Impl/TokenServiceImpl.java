package com.web.socket.service.Impl;

import com.web.socket.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

//    private final TokenRedisRepository tokenRedisRepository;
//
//    @Override
//    @Transactional
//    public void save(Token token) {
//        tokenRedisRepository.save(token);
//    }
//
//    @Override
//    public void delete(String uuid) {
//        tokenRedisRepository.deleteById(uuid);
//    }
//
//    @Override
//    @Transactional
//    public Token get(String uuid) {
//        return tokenRedisRepository.findById(uuid).orElse(null);
//    }
//
//    @Override
//    @Transactional
//    public void invalidateAllUserToken(Integer userId) {
//        List<Token> tokens = tokenRedisRepository.findByUserKey(userId);
//        tokenRedisRepository.deleteAllById(tokens.stream().map(Token::getUuid).toList());
//    }
}
