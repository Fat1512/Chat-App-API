package com.web.socket.service.Impl;

import com.web.socket.dto.UserProfileDTO;
import com.web.socket.entity.ChatRoom;
import com.web.socket.entity.User;
import com.web.socket.exception.InvalidCredential;
import com.web.socket.exception.ResourceNotFoundException;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.UserService;
import com.web.socket.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final AmazonS3Service amazonS3Service;
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredential("User not found"));
        if(user == null) {
            throw new InvalidCredential("user not found");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("CUSTOMER"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }


    @Override
    public UserProfileDTO getProfile(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user doesn't exist"));
        return UserProfileDTO
                .builder()
                .id(user.getId())
                .onlineStatus(user.getStatus().isOnline())
                .avt(user.getAvatar())
                .bio(user.getBio())
                .name(user.getName())
                .username(user.getUsername())
                .chatRoomIds(user.getChatRooms().stream().map(ChatRoom::getId).toList())
                .build();
    }

    @Override
    public UserProfileDTO getProfile()  {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user doesn't exist"));

        return UserProfileDTO
                .builder()
                .id(user.getId())
                .onlineStatus(user.getStatus().isOnline())
                .avt(user.getAvatar())
                .bio(user.getBio())
                .name(user.getName())
                .username(user.getUsername())
                .chatRoomIds(user.getChatRooms().stream().map(ChatRoom::getId).toList())
                .build();
    }

    @Override
    public Boolean isBelongToChatRoom(String chatRoomId) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user doesn't exist"));

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(user.getId()).and("chatRooms").in(new ObjectId(chatRoomId)));
        return mongoTemplate.exists(query, User.class);
    }

    @Override
    public String uploadAvatar(MultipartFile multipartFile) throws IOException {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("user doesn't exist"));

        String avatarUrl = amazonS3Service.uploadImage(multipartFile, String.format("user-avt/%s", authenticatedUser.getId()));
        authenticatedUser.setAvatar(avatarUrl);
        userRepository.save(authenticatedUser);
        return avatarUrl;
    }
}




























