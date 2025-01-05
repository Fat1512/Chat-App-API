package com.web.socket.service.Impl;

import com.web.socket.dto.response.ChatRoomSummaryDTO;
import com.web.socket.dto.response.GeneralUserProfileDTO;
import com.web.socket.dto.response.MessageDTO;
import com.web.socket.dto.response.MessageStatusDTO;
import com.web.socket.entity.ChatRoom;
import com.web.socket.entity.Message;
import com.web.socket.entity.UnreadMessage;
import com.web.socket.entity.User;
import com.web.socket.exception.BadRequestException;
import com.web.socket.exception.ResourceNotFoundException;
import com.web.socket.repository.ChatRoomRepository;
import com.web.socket.repository.MessageRepository;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.ChatRoomService;
import com.web.socket.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public List<ChatRoomSummaryDTO> getChatRoomSummary() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails)authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));

         return authenticatedUser.getChatRooms()
            .stream()
            .map(chatRoom -> {
                //Get the lastest message per chatroom
                Message message = chatRoom.getMessageHistory()
                        .stream()
                        .flatMap(history -> history.getMessages().stream())
                        .max(Comparator.comparing(Message::getTimeSent))
                        .orElse(null);

                if(message == null) {
                    User sender = chatRoom.getMembers().stream()
                            .filter(member -> !member.getId().equals(authenticatedUser.getId())).findFirst().get();
                    return ChatRoomSummaryDTO
                            .builder()
                            .chatRoomId(chatRoom.getId().toString())
                            .roomType(chatRoom.getRoomType())
                            .unreadMessage(null)
                            .lastestMessage(null)
                            .userProfile(GeneralUserProfileDTO
                                    .builder()
                                    .id(sender.getId().toString())
                                    .name(sender.getName())
                                    .username(sender.getUsername())
                                    .status(sender.getStatus())
                                    .bio(sender.getBio())
                                    .avatar(sender.getAvatar())
                                    .build())
                            .build();
                }

                User sender = message.getSender();
                MessageDTO lastestMessage = MessageDTO
                        .builder()
                        .id(message.getId().toString())
                        .messageType(message.getMessageType())
                        .content(message.getContent())
                        .timeSent(message.getTimeSent())
                        .imageUrl(message.getImageUrl())
                        .callDetails(message.getCallDetails())
                        .voiceDetail(message.getVoiceDetail())
                        .undeliveredMembersId(message.getUndeliveredMembers().stream().map(member -> member.getId().toString()).toList())
                        .unreadMembersId(message.getUnreadMembers().stream().map(member -> member.getId().toString()).toList())
                        .senderId(sender.getId().toString())
                        .build();
                List<MessageStatusDTO> unreadMessages = authenticatedUser.getUnreadMessages()
                        .stream()
                        .filter(unreadMessage -> unreadMessage.getChatRoom().getId().equals(chatRoom.getId()))
                        .map(msg -> MessageStatusDTO
                                .builder()
                                .chatRoomId(chatRoom.getId().toString())
                                .messageId(msg.getMessage().getId().toString())
                                .build()).toList();

                GeneralUserProfileDTO userProfile = GeneralUserProfileDTO
                        .builder()
                        .id(sender.getId().toString())
                        .name(sender.getName())
                        .username(sender.getUsername())
                        .status(sender.getStatus())
                        .bio(sender.getBio())
                        .avatar(sender.getAvatar())
                        .build();

                return ChatRoomSummaryDTO
                        .builder()
                        .chatRoomId(chatRoom.getId().toString())
                        .roomType(chatRoom.getRoomType())
                        .unreadMessage(unreadMessages)
                        .lastestMessage(lastestMessage)
                        .userProfile(userProfile)
                        .build();
            }).toList();
    }

    @Override
    @Transactional
    public MessageDTO pushMessageToChatRoom(MessageDTO messageDTO, String chatRoomId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails)authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        ChatRoom chatRoom = chatRoomRepository.findById(new ObjectId(chatRoomId))
                .orElseThrow(() -> new ResourceNotFoundException("chat room doesn't exist"));

        if(!chatRoom.getMembers().stream().anyMatch(user -> user.getId().equals(authenticatedUser.getId()))) {
            throw new BadRequestException("You are not allowed to perform this action");
        };

        Message message = Message
                .builder()
                .messageType(messageDTO.getMessageType())
                .content(messageDTO.getContent())
                .timeSent((double) Instant.now().toEpochMilli())
                .imageUrl(messageDTO.getImageUrl())
                .sender(authenticatedUser)
                .build();


        List<User> remainingMembers = chatRoom.getMembers().stream()
                .filter(member -> {
                    if(!member.getId().equals(authenticatedUser.getId())) {
                        member.getUnreadMessages().add(UnreadMessage
                                .builder()
                                .message(message)
                                .chatRoom(chatRoom)
                                .build());
                        return true;
                    }
                    return false;
                })
                .toList();

        message.getUndeliveredMembers().addAll(remainingMembers);
        message.getUnreadMembers().addAll(remainingMembers);

        Double today = (double)LocalDate.now()
                .atStartOfDay(ZoneOffset.UTC) // Start of day in UTC
                .toInstant()
                .toEpochMilli();

        ChatRoom.MessageHistory messageHistory = chatRoom.getMessageHistory().stream()
                .filter(messageHistoryy -> messageHistoryy.getDay().equals(today))
                .findFirst()
                .orElse(null);

        if(messageHistory == null) {
            messageHistory = ChatRoom.MessageHistory
                    .builder()
                    .day(today)
                    .messages(List.of(message))
                    .build();
            chatRoom.getMessageHistory().add(messageHistory);
        } else {
            messageHistory.getMessages().add(message);
        }
        messageRepository.save(message);
        chatRoomRepository.save(chatRoom);
        userRepository.saveAll(remainingMembers);
        return MessageDTO
                .builder()
                .id(message.getId().toString())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .timeSent(message.getTimeSent())
                .imageUrl(message.getImageUrl())
                .callDetails(message.getCallDetails())
                .voiceDetail(message.getVoiceDetail())
                .undeliveredMembersId(message.getUndeliveredMembers().stream()
                        .map(member -> member.getId().toString()).toList())
                .unreadMembersId(message.getUnreadMembers().stream()
                        .map(member -> member.getId().toString()).toList())
                .senderId(authenticatedUser.getId().toString())
                .build();
    }

    @Override
    @Transactional
    public List<MessageStatusDTO> markReadMessage(List<String> messagesId, String chatRoomId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails)authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        ChatRoom chatRoom = chatRoomRepository.findById(new ObjectId(chatRoomId))
                .orElseThrow(() -> new ResourceNotFoundException("Chat room doesn't exist"));

        //Delete all the unread messages
        authenticatedUser.setUnreadMessages(new ArrayList<>());
        //Remove unread user from chat room messages
        List<MessageStatusDTO> messageStatusList = chatRoom.getMessageHistory().stream()
                .flatMap(msgHistory -> msgHistory.getMessages().stream())
                .filter(filteredMsg -> messagesId.contains(filteredMsg.getId()))
                .map(msg -> {
                            msg.getUnreadMembers()
                                    .removeIf(unreadMem -> unreadMem.getId().equals(authenticatedUser.getId()));
                            return MessageStatusDTO
                                    .builder()
                                    .chatRoomId(chatRoomId)
                                    .messageId(msg.getId().toString())
                                    .unreadMembers(msg.getUnreadMembers().stream()
                                            .map(unreadMem -> unreadMem.getId().toString()).toList())
                                    .undeliveredMembers(msg.getUndeliveredMembers().stream()
                                            .map(unreadMem -> unreadMem.getId().toString()).toList())
                                    .build();
                        }
                ).toList();
        chatRoomRepository.save(chatRoom);
        userRepository.save(authenticatedUser);
        return messageStatusList;
    }
}












































