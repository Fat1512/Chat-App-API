package com.web.socket.repository;

import com.web.socket.entity.BlockToken;
import com.web.socket.entity.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockTokenRedisRepository extends CrudRepository<BlockToken, String> {
    List<Token> findAllByUserKey(String userKey);
    void deleteAllByUserKey(String userKey);
    void deleteAll(List<String> uuid);
}