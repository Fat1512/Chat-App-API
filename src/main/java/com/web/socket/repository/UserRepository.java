package com.web.socket.repository;

import com.web.socket.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Query("{'id': {'$in': ?0}}")
    List<User> findByUserIds(List<String> userids);
}
