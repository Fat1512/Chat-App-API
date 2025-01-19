package com.web.socket.service.Impl;

import com.web.socket.dto.*;
import com.web.socket.entity.*;
import com.web.socket.exception.BadRequestException;
import com.web.socket.exception.ResourceNotFoundException;
import com.web.socket.repository.ChatRoomRepository;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.ChatRoomService;
import com.web.socket.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public List<ChatRoomSummaryDTO> getChatRoomSummary() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        return authenticatedUser.getChatRooms()
                .stream()
                .filter(chatRoom -> !chatRoom.getMessageHistory().isEmpty())
                .map(chatRoom -> {
                    //Get the lastest message per chatroom
                    Message message = chatRoom.getMessageHistory()
                            .stream()
                            .flatMap(history -> history.getMessages().stream())
                            .max(Comparator.comparing(Message::getTimeSent))
                            .get();

                    Boolean deliveredStatus = message.getUndeliveredMembers().isEmpty();
                    Boolean readStatus = message.getUnreadMembers().isEmpty();

                    User sender = message.getSender();
                    MessageDTO lastestMessage = MessageDTO
                            .builder()
                            .id(message.getId())
                            .messageType(message.getMessageType())
                            .content(message.getContent()).timeSent(message.getTimeSent())
                            .imageUrl(message.getImageUrl())
                            .callDetails(message.getCallDetails())
                            .voiceDetail(message.getVoiceDetail())
                            .undeliveredMembersId(message.getUndeliveredMembers().stream().map(User::getId).toList())
                            .unreadMembersId(message.getUnreadMembers().stream().map(User::getId).toList())
                            .deliveredStatus(deliveredStatus)
                            .readStatus(readStatus)
                            .senderId(sender.getId()).build();

                    Integer TotalUnreadMessages = Math.toIntExact(authenticatedUser.getUnreadMessages()
                            .stream()
                            .filter(msg -> msg.getChatRoomId().equals(chatRoom.getId())).count());
                    User userFriend = chatRoom.getMembers()
                            .stream()
                            .filter(member -> !member.getId().equals(authenticatedUser.getId())).findFirst().get();
                    GeneralUserProfileDTO userProfile = GeneralUserProfileDTO
                            .builder()
                            .id(userFriend.getId())
                            .name(userFriend.getName())
                            .username(userFriend.getUsername()).status(userFriend.getStatus())
                            .bio(userFriend.getBio())
                            .avatar(userFriend.getAvatar())
                            .build();

                    return ChatRoomSummaryDTO
                            .builder()
                            .chatRoomId(chatRoom.getId())
                            .roomType(chatRoom.getRoomType())
                            .totalUnreadMessages(TotalUnreadMessages)
                            .lastestMessage(lastestMessage)
                            .userProfile(userProfile)
                            .build();
                }).toList();
    }

    @Override
    public ChatRoomDetailDTO getChatRoomDetail(String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ResourceNotFoundException("Invalid chatroom"));

        List<ChatRoomDetailDTO.MessageHistoryDTO> messageHistory = chatRoom.getMessageHistory()
                .stream()
                .map(msgHistory -> {
                    Double day = msgHistory.getDay();
                    List<MessageDTO> messages = msgHistory.getMessages().stream()
                            .map(msg -> MessageDTO.builder()
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
                    return ChatRoomDetailDTO.MessageHistoryDTO
                            .builder()
                            .day(day)
                            .messages(messages)
                            .build();
                }).toList();
//        Map<Double, List<MessageDTO>> messageHistory = chatRoom.getMessageHistory()
//                .stream()
//                .collect(Collectors.toMap(
//                        ChatRoom.MessageHistory::getDay,
//                        msgHistory -> msgHistory.getMessages()
//                                .stream()
//                                .map(msg -> MessageDTO.builder()
//                                        .id(msg.getId())
//                                        .messageType(msg.getMessageType())
//                                        .content(msg.getContent())
//                                        .timeSent(msg.getTimeSent())
//                                        .imageUrl(msg.getImageUrl())
//                                        .callDetails(msg.getCallDetails())
//                                        .voiceDetail(msg.getVoiceDetail())
//                                        .undeliveredMembersId(msg.getUndeliveredMembers().stream().map(User::getId).toList())
//                                        .unreadMembersId(msg.getUnreadMembers().stream().map(User::getId).toList())
//                                        .deliveredStatus(msg.getUndeliveredMembers().isEmpty())
//                                        .readStatus(msg.getUnreadMembers().isEmpty())
//                                        .senderId(msg.getSender().getId()).build()).toList()));

        return ChatRoomDetailDTO.builder()
                .chatRoomId(chatRoom.getId())
                .roomType(chatRoom.getRoomType())
                .messageHistory(messageHistory)
                .build();
    }

    @Override
    @Transactional
    public MessageDTO pushMessageToChatRoom(MessageDTO messageDTO, String chatRoomId) {

        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository
                .findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        ChatRoom chatRoom = chatRoomRepository
                .findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("chat room doesn't exist"));

        if (chatRoom.getMembers().stream().noneMatch(user -> user.getId().equals(authenticatedUser.getId()))) {
            throw new BadRequestException("You are not allowed to perform this action");
        }


        Message message = Message
                .builder()
                .messageType(messageDTO.getMessageType())
                .content(messageDTO.getContent()).timeSent((double) Instant.now().toEpochMilli())
                .imageUrl(messageDTO.getImageUrl())
                .sender(authenticatedUser)
                .build();

        List<User> remainingMembers = chatRoom.getMembers().stream().filter(member -> {
            if (!member.getId().equals(authenticatedUser.getId())) {
                member.getUnreadMessages().add(UnreadMessage.builder()
                        .messageId(message.getId())
                        .chatRoomId(chatRoom.getId())
                        .build());
                member.getUndeliveredMessages().add(UndeliveredMessage.builder()
                        .messageId(message.getId())
                        .chatRoomId(chatRoom.getId())
                        .build());
                return true;
            }
            return false;
        }).toList();

        message.getUndeliveredMembers().addAll(remainingMembers);
        message.getUnreadMembers().addAll(remainingMembers);

        Double today = (double) LocalDate.now().atStartOfDay(ZoneOffset.UTC) // Start of day in UTC
                .toInstant().toEpochMilli();

        ChatRoom.MessageHistory messageHistory = chatRoom.getMessageHistory()
                .stream()
                .filter(messageHistoryy -> messageHistoryy.getDay().equals(today))
                .findFirst()
                .orElse(null);

        if (messageHistory == null) {
            messageHistory = ChatRoom.MessageHistory.builder().day(today).messages(List.of(message)).build();
            chatRoom.getMessageHistory().add(messageHistory);
        } else {
            messageHistory.getMessages().add(message);
        }

        chatRoomRepository.save(chatRoom);
        userRepository.saveAll(remainingMembers);

        return MessageDTO.builder()
                .id(message.getId())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .timeSent(message.getTimeSent())
                .imageUrl(message.getImageUrl())
                .callDetails(message.getCallDetails())
                .voiceDetail(message.getVoiceDetail())
                .undeliveredMembersId(message.getUndeliveredMembers().stream().map(User::getId).toList())
                .unreadMembersId(message.getUnreadMembers().stream().map(User::getId).toList())
                .deliveredStatus(message.getUndeliveredMembers().isEmpty())
                .readStatus(message.getUnreadMembers().isEmpty())
                .senderId(authenticatedUser.getId())
                .build();
    }

    @Override
    @Transactional
    public List<MessageStatusDTO> markDeliveredMessages(String chatRoomId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));
        log.info("current undelivered authenticated user: {}", username);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room doesn't exist"));


        List<String> undeliveredMessageIds = authenticatedUser.getUndeliveredMessages()
                .stream()
                .filter(undeliveredMessage -> undeliveredMessage.getChatRoomId().equals(chatRoomId))
                .map(UndeliveredMessage::getMessageId)
                .toList();

        //Remove all the undelivered messages from current user
        authenticatedUser.getUndeliveredMessages()
                .removeIf(undeliveredMessage -> undeliveredMessageIds.contains(undeliveredMessage.getMessageId()));

        //Remove undelivered user from chat room's messages
        List<MessageStatusDTO> messageStatusList = chatRoom.getMessageHistory()
                .stream()
                .flatMap(msgHistory -> msgHistory.getMessages().stream())
                .filter(filteredMsg -> undeliveredMessageIds.contains(filteredMsg.getId()))
                .map(msg -> {
                    msg.getUndeliveredMembers()
                            .removeIf(unreadMem -> unreadMem.getId().equals(authenticatedUser.getId()));
                    return MessageStatusDTO
                            .builder()
                            .chatRoomId(chatRoomId)
                            .messageId(msg.getId())
                            .unreadMembers(msg.getUnreadMembers().stream().map(User::getId).toList())
                            .undeliveredMembers(msg.getUndeliveredMembers().stream().map(User::getId).toList())
                            .senderId(authenticatedUser.getId())
                            .build();
                }).toList();

        chatRoomRepository.save(chatRoom);
        userRepository.save(authenticatedUser);
        return messageStatusList;
    }

    @Override
    public List<MessageStatusDTO> markReadMessages(String chatRoomId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        log.info("current read authenticated user: {}", username);
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room doesn't exist"));

        List<String> unreadMessageIds = authenticatedUser.getUnreadMessages()
                .stream()
                .filter(unreadMessage -> unreadMessage.getChatRoomId().equals(chatRoomId))
                .map(UnreadMessage::getMessageId)
                .toList();

        List<String> undeliveredMessageIds = authenticatedUser.getUndeliveredMessages()
                .stream()
                .filter(undeliveredMessage -> undeliveredMessage.getChatRoomId().equals(chatRoomId))
                .map(UndeliveredMessage::getMessageId)
                .toList();

        //Remove all the unread messages from current user
        authenticatedUser.getUnreadMessages()
                .removeIf(unreadMessage -> unreadMessageIds.contains(unreadMessage.getMessageId()));
        authenticatedUser.getUndeliveredMessages()
                .removeIf(undeliveredMessage -> undeliveredMessageIds.contains(undeliveredMessage.getMessageId()));

        //Remove unread user from chat room's messages
        List<MessageStatusDTO> messageStatusList = chatRoom.getMessageHistory()
                .stream()
                .flatMap(msgHistory -> msgHistory.getMessages().stream())
                .filter(filteredMsg -> unreadMessageIds.contains(filteredMsg.getId()) || undeliveredMessageIds.contains(filteredMsg.getId()))
                .map(msg -> {
                    msg.getUndeliveredMembers().removeIf(unreadMem -> unreadMem.getId().equals(authenticatedUser.getId()));
                    msg.getUnreadMembers().removeIf(unreadMem -> unreadMem.getId().equals(authenticatedUser.getId()));

                    return MessageStatusDTO
                            .builder()
                            .chatRoomId(chatRoomId)
                            .messageId(msg.getId())
                            .unreadMembers(msg.getUnreadMembers().stream().map(User::getId).toList())
                            .undeliveredMembers(msg.getUndeliveredMembers().stream().map(User::getId).toList())
                            .senderId(authenticatedUser.getId())
                            .build();
                }).toList();

        chatRoomRepository.save(chatRoom);
        userRepository.save(authenticatedUser);
        return messageStatusList;
    }

    @Override
    @Transactional
    public void broadcastOfflineStatus() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));
        authenticatedUser.setStatus(User.UserStatus
                .builder()
                .online(false)
                .lastSeen((double) Instant.now().toEpochMilli())
                .build());
        mongoTemplate.save(authenticatedUser, "user");
        authenticatedUser.getChatRooms().stream().forEach(chatRoom -> {
            simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoom.getId() + "/onlineStatus",
                    OnlineStatusDTO
                            .builder()
                            .chatRoomId(chatRoom.getId())
                            .senderId(authenticatedUser.getId())
                            .status(authenticatedUser.getStatus().isOnline())
                            .lastSeen(authenticatedUser.getStatus().getLastSeen())
                            .build());
        });
    }

    @Override
    @Transactional
    public void broadcastOnlineStatus() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));
        authenticatedUser.setStatus(User.UserStatus
                .builder()
                .online(true)
                .build());
        mongoTemplate.save(authenticatedUser, "user");
        authenticatedUser.getChatRooms().stream().forEach(chatRoom -> {
            simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoom.getId() + "/onlineStatus",
                    OnlineStatusDTO
                            .builder()
                            .chatRoomId(chatRoom.getId())
                            .senderId(authenticatedUser.getId())
                            .status(authenticatedUser.getStatus().isOnline())
                            .lastSeen(authenticatedUser.getStatus().getLastSeen())
                            .build());
        });
    }
}












































