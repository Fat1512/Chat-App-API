package com.web.socket.service.Impl;


import com.web.socket.dto.LoginRequest;
import com.web.socket.dto.RegisterRequest;
import com.web.socket.dto.response.TokenResponse;
import com.web.socket.dto.response.UserAuthResponse;
import com.web.socket.entity.User;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserAuthResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if(username == null || password == null)
            throw new BadCredentialsException("Wrong username or password");

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetail = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(userDetail.getUsername()).get();

        TokenResponse tokenResponse = jwtService.generateToken(userDetail);

        return UserAuthResponse.builder()
                .id(user.getId().toString())
                .onlineStatus(true)
                .name(user.getName())
                .username(user.getUsername())
                .bio(user.getBio())
                .avt(user.getAvatar())
                .isAuthenticated(true)
                .tokenResponse(tokenResponse)
                .build();
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        String name = registerRequest.getName();
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String confirmedPassword = registerRequest.getConfirmedPassword();

        if(name == null || username == null || password == null || confirmedPassword == null)
            throw new BadCredentialsException("Fields must not be null");
        if(!password.equals(confirmedPassword))
            throw new BadCredentialsException("Password doesn't match each other");

        User user = userRepository.findByUsername(username).orElse(null);

        if(user != null) {
            throw new BadCredentialsException("Username existed !");
        }

        user = User.builder()
                .name(name)
                .username(username)
                .password(passwordEncoder.encode(password))
                .status(new User.UserStatus())
                .build();
        userRepository.save(user);
    }

}
