package com.web.socket.repository;

import com.web.socket.entity.ChatRoom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, ObjectId> {

//    @Query(value = "{'messageHistory.messages.sender' : ?0, '_id' :  }")
//    ChatRoom findByIdAndUser(String userId, String chatRoomId);
}
