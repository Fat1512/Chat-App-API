package com.web.socket.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.socket.entity.Token;
import com.web.socket.service.Impl.JwtService;
import com.web.socket.service.Impl.UserServiceImpl;
import com.web.socket.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Lazy
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserServiceImpl userService;
//    private final TaskScheduler messageBrokerTaskScheduler;
//    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
//                .setTaskScheduler(this.messageBrokerTaskSche  duler);
//                .setHeartbeatValue(new long[] {0, 12000}); //outgoing - incoming
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("ws")//connection establishment
                .setAllowedOrigins("http://localhost:5173");
        registry.addEndpoint("ws")
                .setAllowedOrigins("http://localhost:5173")
                .withSockJS();//connection establishment
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(100240 * 10240);
        registry.setSendBufferSizeLimit(100240 * 10240);
        registry.setSendTimeLimit(20000);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        System.out.println("Disconnected session: " + sessionId);

        // Notify other users or perform cleanup tasks
        // Example: send user offline message
//        messagingTemplate.convertAndSend("/topic/status", "User disconnected: " + sessionId);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(10);
        registration.taskExecutor().corePoolSize(20);

        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                log.info("Headers: {}", accessor);
                if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                    log.info("ignore disconnect frame to avoid double disconnect: {}", message);
                }
                if (StompCommand.CONNECT.equals(accessor.getCommand())
                        || StompCommand.SUBSCRIBE.equals(accessor.getCommand())
                        || StompCommand.SEND.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    token = jwtService.extractToken(token);
                    if (jwtService.validateToken(token)) {

                        String username = jwtService.extractUsername(token);
                        String userKey = jwtService.extractUserId(token);
                        String uuid = jwtService.extractUuid(token);

                        UserDetails userDetails = userService.loadUserByUsername(username);
                        List<Token> tokens = tokenService.findAllByUserKey(userKey);
                        if(tokens == null || tokens.isEmpty()) return message;

                        Map<Boolean, List<Token>> partitionedTokens = tokens.stream()
                                .collect(Collectors
                                        .partitioningBy(filterToken -> filterToken.getUuid().equals(uuid)));

                        Token redisToken = partitionedTokens.get(true)
                                .stream()
                                .findFirst()
                                .orElse(null);

                        if (userDetails != null && redisToken != null) {
                            List<Token> remainingTokens = partitionedTokens.get(false);
                            if(!remainingTokens.isEmpty()) {
                                tokenService.deleteAll(remainingTokens);
                            }

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            accessor.setUser(authToken);
                        }

                    }
                }

                return message;
            }
        });
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setContentTypeResolver(resolver);
        converter.setObjectMapper(new ObjectMapper());
        messageConverters.add(converter);
        return false;
    }
}














































