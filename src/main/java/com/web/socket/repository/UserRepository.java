package com.web.socket.repository;

import com.web.socket.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ '$or': [ { 'username': ?0 }, { 'email': ?0 } ] }")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("{'id': {'$in': ?0}}")
    List<User> findByUserIds(List<String> userids);

    boolean existsByIdAndChatRoomsId(String userId, String chatRoomId);
}
