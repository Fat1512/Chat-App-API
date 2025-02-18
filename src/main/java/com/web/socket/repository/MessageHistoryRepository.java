package com.web.socket.repository;

import com.web.socket.entity.Message;
import com.web.socket.entity.MessageHistory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface MessageHistoryRepository extends MongoRepository<MessageHistory, ObjectId> {
}
