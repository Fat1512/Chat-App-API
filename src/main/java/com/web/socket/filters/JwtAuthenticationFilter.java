package com.web.socket.filters;

import com.web.socket.entity.Token;
import com.web.socket.exception.ResourceNotFoundException;
import com.web.socket.service.Impl.JwtService;
import com.web.socket.service.Impl.UserServiceImpl;
import com.web.socket.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenService tokenService;
    private final UserServiceImpl userService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if(token == null || !token.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            token = jwtService.extractToken(token);
            //Validate the expired date, necessary field...
            if(jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                String uuid = jwtService.extractUuid(token);
                Token redisToken = tokenService.get(uuid);

                if (redisToken == null)
                    throw new AccessDeniedException("Token not existed");

                UserDetails userDetails = userService.loadUserByUsername(username);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                filterChain.doFilter(request, response);
            } else
                throw new AccessDeniedException("Expired token");
        } catch (AccessDeniedException runtimeException) {
            handlerExceptionResolver.resolveException(request, response, null, runtimeException);
        }
    }
}
