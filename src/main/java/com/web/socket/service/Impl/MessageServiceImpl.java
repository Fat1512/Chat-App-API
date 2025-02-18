package com.web.socket.service.Impl;

import com.web.socket.dto.ChatRoomDetailDTO;
import com.web.socket.dto.response.MessageResponse;
import com.web.socket.entity.MessageHistory;
import com.web.socket.entity.User;
import com.web.socket.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<ChatRoomDetailDTO.MessageHistoryDTO> getMessages(String chatRoomId, Integer pageSize, Integer page, Integer paddingOffset) {
        //Message Pagination
        MatchOperation match = Aggregation.match(Criteria.where("chatRoomId").is(new ObjectId(chatRoomId)));
        UnwindOperation unwind = Aggregation.unwind("messages");
        SortOperation sortByDay = Aggregation.sort(Sort.Direction.DESC, "day");
        ProjectionOperation project = Aggregation.project("chatRoomId", "day", "messages");

        SkipOperation skipOp = Aggregation.skip((long) (page - 1) * pageSize);
        LimitOperation limitOp = Aggregation.limit(pageSize);

        GroupOperation group = Aggregation.group("day")
                .first("chatRoomId").as("chatRoomId")
                .first("day").as("day")
                .push("messages").as("messages");

        SortOperation finalSort = Aggregation.sort(Sort.Direction.ASC, "day");

        Aggregation aggregation = Aggregation.newAggregation(
                match,
                unwind,
                sortByDay,
                project,
                skipOp,
                limitOp,
                group,
                finalSort
        );
        List<MessageHistory> messageHistories = mongoTemplate.aggregate(aggregation, "messageHistory", MessageHistory.class)
                .getMappedResults();

        return messageHistories.stream()
                .map(msgHistory -> {
                    Double day = msgHistory.getDay();
                    List<MessageResponse> messages = msgHistory.getMessages().stream().map(msg -> MessageResponse
                            .builder()
                            .id(msg.getId())
                            .messageType(msg.getMessageType())
                            .content(msg.getContent())
                            .timeSent(msg.getTimeSent())
                            .imageUrl(msg.getImageUrl())
                            .callDetails(msg.getCallDetails())
                            .voiceDetail(msg.getVoiceDetail())
                            .undeliveredMembersId(msg.getUndeliveredMembers().stream().map(User::getId).toList())
                            .unreadMembersId(msg.getUnreadMembers().stream().map(User::getId).toList())
                            .deliveredStatus(msg.getUndeliveredMembers().isEmpty())
                            .readStatus(msg.getUnreadMembers().isEmpty())
                            .senderId(msg.getSender().getId()).build()).toList();
                    return ChatRoomDetailDTO.MessageHistoryDTO.builder().day(day).messages(messages).build();
                }).toList();
    }
}
