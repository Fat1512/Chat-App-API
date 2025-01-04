package com.web.socket.repository;

import com.web.socket.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestRepository extends MongoRepository<User, String> {
}
