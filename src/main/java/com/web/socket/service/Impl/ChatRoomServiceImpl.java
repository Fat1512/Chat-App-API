package com.web.socket.service.Impl;

import com.web.socket.dto.*;
import com.web.socket.dto.request.MessageRequest;
import com.web.socket.dto.response.MessageResponse;
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
    private final AmazonS3Service amazonS3Service;

    @Override
    public List<ChatRoomSummaryDTO> getChatRoomSummary() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        return authenticatedUser.getChatRooms().stream().map(chatRoom -> {
            //Get the lastest message per chatroom
            Message message = chatRoom.getMessageHistory()
                    .stream()
                    .flatMap(history -> history.getMessages().stream())
                    .max(Comparator.comparing(Message::getTimeSent))
                    .orElse(null);

            //Newly created group chat doesn't have value but should be retrieved
            MessageResponse lastestMessage = null;
            Integer TotalUnreadMessages = 0;
            if (message != null) {

                Boolean deliveredStatus = message.getUndeliveredMembers().isEmpty();
                Boolean readStatus = message.getUnreadMembers().isEmpty();

                lastestMessage = MessageResponse.builder()
                        .id(message.getId())
                        .messageType(message.getMessageType())
                        .content(message.getContent())
                        .timeSent(message.getTimeSent())
                        .imageUrl(message.getImageUrl())
                        .callDetails(message.getCallDetails())
                        .voiceDetail(message.getVoiceDetail())
                        .undeliveredMembersId(message.getUndeliveredMembers().stream().map(User::getId).toList())
                        .unreadMembersId(message.getUnreadMembers().stream().map(User::getId).toList())
                        .deliveredStatus(deliveredStatus)
                        .readStatus(readStatus)
                        .senderId(message.getSender().getId())
                        .build();

                TotalUnreadMessages = Math.toIntExact(authenticatedUser.getUnreadMessages().stream()
                        .filter(msg -> msg.getChatRoomId().equals(chatRoom.getId())).count());
            }

            Object groupInfo = switch (chatRoom.getRoomType().toString()) {
                case "PRIVATE" -> {
                    User userFriend = chatRoom.getMembers()
                            .stream()
                            .filter(member -> !member.getId().equals(authenticatedUser.getId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Not found"));
                    yield SingleProfileDTO
                            .builder()
                            .roomType("PRIVATE")
                            .id(userFriend.getId())
                            .name(userFriend.getName())
                            .username(userFriend.getUsername())
                            .status(userFriend.getStatus())
                            .bio(userFriend.getBio())
                            .avatar(userFriend.getAvatar())
                            .build();
                }
                case "GROUP" -> GroupProfileDTO
                        .builder()
                        .roomType("GROUP")
                        .name(chatRoom.getGroupName())
                        .avatar(chatRoom.getGroupAvatar())
                        .build();
                default -> null;
            };
            return ChatRoomSummaryDTO
                    .builder()
                    .chatRoomId(chatRoom.getId())
                    .roomType(chatRoom.getRoomType())
                    .totalUnreadMessages(TotalUnreadMessages)
                    .lastestMessage(lastestMessage)
                    .roomInfo(groupInfo)
                    .build();
        }).toList();
    }

    @Override
    public ChatRoomDetailDTO getChatRoomDetail(String chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid chatroom"));

        List<ChatRoomDetailDTO.MessageHistoryDTO> messageHistory = chatRoom
                .getMessageHistory()
                .stream()
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
        List<GeneralUserProfileDTO> members = chatRoom.getMembers()
                .stream().map(mem -> GeneralUserProfileDTO
                .builder()
                .id(mem.getId())
                .name(mem.getName())
                .username(mem.getUsername())
                .status(mem.getStatus())
                .bio(mem.getBio())
                .avatar(mem.getAvatar())
                .build()).toList();

        return ChatRoomDetailDTO
                .builder()
                .chatRoomId(chatRoom.getId())
                .roomType(chatRoom.getRoomType())
                .messageHistory(messageHistory)
                .members(members)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse pushMessageToChatRoom(MessageRequest messageRequest, String chatRoomId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        ChatRoom chatRoom = chatRoomRepository
                .findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("chat room doesn't exist"));

        if (chatRoom.getMembers().stream()
                .noneMatch(user -> user.getId().equals(authenticatedUser.getId()))) {
            throw new BadRequestException("Not allowed to perform this action");
        }


        Message message = Message
                .builder()
                .messageType(messageRequest.getMessageType())
                .content(messageRequest.getContent())
                .callDetails(messageRequest.getCallDetails())
                .voiceDetail(messageRequest.getVoiceDetail())
                .timeSent((double) Instant.now().toEpochMilli())
                .imageUrl(messageRequest.getImageUrl())
                .sender(authenticatedUser).build();

        List<User> remainingMembers = chatRoom.getMembers().stream().filter(member -> {
            if (!member.getId().equals(authenticatedUser.getId())) {
                member.getUnreadMessages()
                        .add(UnreadMessage.builder()
                                .messageId(message.getId())
                                .chatRoomId(chatRoom.getId())
                                .build());
                member.getUndeliveredMessages()
                        .add(UndeliveredMessage.builder()
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
                .filter(msgHistory -> msgHistory.getDay().equals(today))
                .findFirst()
                .orElse(null);

        if (messageHistory == null) {
            messageHistory = ChatRoom.MessageHistory
                    .builder()
                    .day(today)
                    .messages(List.of(message))
                    .build();
            chatRoom.getMessageHistory().add(messageHistory);
        } else {
            messageHistory.getMessages().add(message);
        }

        chatRoomRepository.save(chatRoom);
        userRepository.saveAll(remainingMembers);

        return MessageResponse
                .builder()
                .id(message.getId())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .timeSent(message.getTimeSent())
                .imageUrl(message.getImageUrl())
                .callDetails(message.getCallDetails())
                .voiceDetail(message.getVoiceDetail())
                .undeliveredMembersId(message.getUndeliveredMembers()
                        .stream().map(User::getId).toList())
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
        List<MessageStatusDTO> messageStatusList = chatRoom.getMessageHistory().stream()
                .flatMap(msgHistory -> msgHistory.getMessages().stream())
                .filter(filteredMsg -> undeliveredMessageIds.contains(filteredMsg.getId())).map(msg -> {
            msg.getUndeliveredMembers().removeIf(unreadMem -> unreadMem.getId().equals(authenticatedUser.getId()));
            return MessageStatusDTO.builder()
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
    public List<MessageStatusDTO> markReadMessages(String chatRoomId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        log.info("current read authenticated user: {}", username);
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room doesn't exist"));

        List<String> unreadMessageIds = authenticatedUser.getUnreadMessages().stream()
                .filter(unreadMessage -> unreadMessage.getChatRoomId().equals(chatRoomId))
                .map(UnreadMessage::getMessageId).toList();

        List<String> undeliveredMessageIds = authenticatedUser.getUndeliveredMessages().stream()
                .filter(undeliveredMessage -> undeliveredMessage.getChatRoomId().equals(chatRoomId))
                .map(UndeliveredMessage::getMessageId).toList();

        //Remove all the unread messages from current user
        authenticatedUser.getUnreadMessages().removeIf(unreadMessage -> unreadMessageIds
                .contains(unreadMessage.getMessageId()));
        authenticatedUser.getUndeliveredMessages().removeIf(undeliveredMessage -> undeliveredMessageIds
                .contains(undeliveredMessage.getMessageId()));

        //Remove unread user from chat room's messages
        List<MessageStatusDTO> messageStatusList = chatRoom.getMessageHistory().stream().flatMap(msgHistory -> msgHistory.getMessages().stream())
                .filter(filteredMsg -> unreadMessageIds.contains(filteredMsg.getId()) || undeliveredMessageIds.contains(filteredMsg.getId()))
                .map(msg -> {
            msg.getUndeliveredMembers().removeIf(unreadMem -> unreadMem.getId().equals(authenticatedUser.getId()));
            msg.getUnreadMembers().removeIf(unreadMem -> unreadMem.getId().equals(authenticatedUser.getId()));

            return MessageStatusDTO.builder()
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

//        authenticatedUser.setStatus(User.UserStatus.builder().online(false).lastSeen((double) Instant.now().toEpochMilli()).build());
        authenticatedUser.getStatus().setOnline(false);
        authenticatedUser.getStatus().setLastSeen((double) Instant.now().toEpochMilli());
        mongoTemplate.save(authenticatedUser, "user");

        authenticatedUser.getChatRooms().stream().forEach(chatRoom -> {
            simpMessagingTemplate.convertAndSend("/topic/chatRoom/" + chatRoom.getId() + "/onlineStatus", OnlineStatusDTO
                    .builder().chatRoomId(chatRoom.getId())
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
        authenticatedUser.getStatus().setOnline(true);

        mongoTemplate.save(authenticatedUser, "user");
        authenticatedUser.getChatRooms().stream().forEach(chatRoom -> {
            simpMessagingTemplate
                    .convertAndSend("/topic/chatRoom/" + chatRoom.getId() + "/onlineStatus"
                            , OnlineStatusDTO.builder()
                                    .chatRoomId(chatRoom.getId())
                                    .senderId(authenticatedUser.getId())
                                    .status(authenticatedUser.getStatus().isOnline())
                                    .lastSeen(authenticatedUser.getStatus().getLastSeen())
                                    .build());
        });
    }

    @Override //Haven't checked the appropriate contact list
    @Transactional
    public void createGroup(GroupCreationDTO groupCreationDTO) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));
        groupCreationDTO.getMembersId().add(authenticatedUser.getId());

        List<User> members = userRepository.findByUserIds(groupCreationDTO.getMembersId());
        ChatRoom chatroom = ChatRoom
                .builder()
                .groupName(groupCreationDTO.getGroupName())
                .groupAvatar("https://static.vecteezy.com/system/resources/thumbnails/036/280/651/small_2x/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg")
                .roomType(RoomType.GROUP)
                .members(members)
                .build();
        chatRoomRepository.save(chatroom);

        members.forEach(mem -> mem.getChatRooms().add(chatroom));
        userRepository.saveAll(members);
    }
}












































