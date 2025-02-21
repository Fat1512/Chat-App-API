package com.web.socket.repository;

import com.web.socket.entity.Message;
import com.web.socket.entity.MessageHistory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface MessageHistoryRepository extends MongoRepository<MessageHistory, String> {
    Optional<MessageHistory> findByChatRoomIdAndDay(String chatRoomId, Double day);
    List<MessageHistory> findByChatRoomId(String chatRoomId);
}
