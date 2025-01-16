package com.web.socket.service.Impl;

import com.web.socket.dto.OnlineStatusDTO;
import com.web.socket.dto.UserProfileDTO;
import com.web.socket.entity.User;
import com.web.socket.exception.InvalidCredential;
import com.web.socket.exception.ResourceNotFoundException;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.UserService;
import com.web.socket.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InvalidCredential("User not found"));
        if(user == null) {
            throw new Error("user not found");
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
                .id(user.getId().toString())
                .onlineStatus(user.getStatus().isOnline())
                .avt(user.getAvatar())
                .bio(user.getBio())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

    @Override
    public UserProfileDTO getProfile() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("user doesn't exist"));
        return UserProfileDTO
                .builder()
                .id(user.getId().toString())
                .onlineStatus(user.getStatus().isOnline())
                .avt(user.getAvatar())
                .bio(user.getBio())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

    @Override
    public OnlineStatusDTO switchToOnlineStatus() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));
        authenticatedUser.setStatus(User.UserStatus
                .builder()
                .online(true)
                .build());
        userRepository.save(authenticatedUser);

        return OnlineStatusDTO
                .builder()
                .status(authenticatedUser.getStatus().isOnline())
                .build();
    }

    @Override
    public OnlineStatusDTO switchToOfflineStatus() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Invalid credential"));
        authenticatedUser.setStatus(User.UserStatus
                .builder()
                .online(false)
                .lastSeen(LocalDateTime.now())
                .build());
        userRepository.save(authenticatedUser);

        return OnlineStatusDTO
                .builder()
                .status(authenticatedUser.getStatus().isOnline())
                .lastSeen(authenticatedUser.getStatus().getLastSeen())
                .build();
    }
}




























