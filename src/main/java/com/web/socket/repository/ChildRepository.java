package com.web.socket.repository;

import com.web.socket.entity.Child;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChildRepository extends MongoRepository<Child, String> {
}
