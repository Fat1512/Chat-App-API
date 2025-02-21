package com.web.socket.service.Impl;

import com.web.socket.dto.ChatRoomDetailDTO;
import com.web.socket.dto.response.MessageResponse;
import com.web.socket.dto.response.PageResponse;
import com.web.socket.entity.MessageHistory;
import com.web.socket.entity.User;
import com.web.socket.repository.MessageHistoryRepository;
import com.web.socket.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
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
    private final MessageHistoryRepository messageHistoryRepository;

    @Override
    public PageResponse<ChatRoomDetailDTO.MessageHistoryDTO> getMessages(String chatRoomId, Integer size, Integer page, Integer paddingOffset) {
        MatchOperation match = Aggregation.match(Criteria.where("chatRoomId").is(new ObjectId(chatRoomId)));
        UnwindOperation unwind = Aggregation.unwind("messages");
        CountOperation count = Aggregation.count().as("totalMessages");

        SortOperation sortByTimeSent1 = Aggregation.sort(Sort.Direction.DESC, "messages.timeSent");
        ProjectionOperation project = Aggregation.project("chatRoomId", "day", "messages");

        SkipOperation skipOp = Aggregation.skip(paddingOffset + (long) (page - 1) * size);
        LimitOperation limitOp = Aggregation.limit(size);

        GroupOperation group = Aggregation.group("day")
                .first("chatRoomId").as("chatRoomId")
                .first("day").as("day")
                .push("messages").as("messages");

        SortOperation sortByTimeSent2 = Aggregation.sort(Sort.Direction.ASC, "messages.timeSent");
        SortOperation finalSort = Aggregation.sort(Sort.Direction.ASC, "day");

        Aggregation aggregationMessage = Aggregation.newAggregation(
                match,
                unwind,
                sortByTimeSent1,
                project,
                skipOp,
                limitOp,
                sortByTimeSent2,
                group,
                finalSort
        );
        Aggregation aggregationCount = Aggregation.newAggregation(
                match,
                unwind,
                count
        );

        Integer totalMessages = (Integer) mongoTemplate.aggregate(
                        aggregationCount,
                        "messageHistory",
                        Document.class)
                .getMappedResults().get(0).get("totalMessages");

        List<MessageHistory> messageHistories = mongoTemplate.aggregate(aggregationMessage, "messageHistory", MessageHistory.class)
                .getMappedResults();

        List<ChatRoomDetailDTO.MessageHistoryDTO> messageHistoryDTOS = messageHistories.stream()
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

        return new PageResponse<>(
                messageHistoryDTOS,
                page,
                size,
                totalMessages,
                0,
                (page - 1) * size + size >= totalMessages);
    }
}
