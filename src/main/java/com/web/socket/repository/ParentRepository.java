package com.web.socket.repository;

import com.web.socket.entity.Parent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParentRepository extends MongoRepository<Parent, String> {
}
