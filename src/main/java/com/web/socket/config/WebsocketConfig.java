package com.web.socket.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.socket.service.Impl.JwtService;
import com.web.socket.service.Impl.UserServiceImpl;
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
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Lazy
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;
    private final UserServiceImpl userService;
//    private final TaskScheduler messageBrokerTaskScheduler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
//                .setTaskScheduler(this.messageBrokerTaskScheduler)
//                .setHeartbeatValue(new long[] {0, 7000}); //outgoing - incoming
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
//            Token redisToken = tokenService.get(jwtService.extractUuid(token));
                        UserDetails userDetails = userService.loadUserByUsername(username);
                        if (userDetails != null
//                    && redisToken != null
                        ) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authToken);
//                            SecurityContext context = SecurityContextHolder.createEmptyContext();
//                            context.setAuthentication(authToken);
//                            SecurityContextHolder.getContextHolderStrategy().setContext(context);
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














































