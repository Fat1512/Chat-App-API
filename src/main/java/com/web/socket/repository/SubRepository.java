package com.web.socket.repository;

import com.web.socket.entity.Sub;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubRepository extends MongoRepository<Sub, String> {
}
