package com.web.socket.repository;

import com.web.socket.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

//    @Query(value = "{'messageHistory.messages.sender' : ?0, '_id' :  }")
//    ChatRoom findByIdAndUser(String userId, String chatRoomId);
}
