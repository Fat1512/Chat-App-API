package com.web.socket.filters;

import com.web.socket.entity.Token;
import com.web.socket.service.Impl.JwtService;
import com.web.socket.service.Impl.UserServiceImpl;
import com.web.socket.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if(token == null) {
            filterChain.doFilter(request, response); return;
        }

        token = jwtService.extractToken(token);
        if(jwtService.validateToken(token)) {
            String username = jwtService.extractUsername(token);
            String userKey = jwtService.extractUserId(token);
            String uuid = jwtService.extractUuid(token);

            UserDetails userDetails = userService.loadUserByUsername(username);
            List<Token> tokens = tokenService.findAllByUserKey(userKey);

            if(tokens != null && !tokens.isEmpty()) {
                //Separate the tokens into 2 list to ensure only 1 logged-in user at the same time
                Map<Boolean, List<Token>> partitionedTokens = tokens.stream()
                        .collect(Collectors.partitioningBy(filterToken -> filterToken.getUuid().equals(uuid)));

                Token redisToken = partitionedTokens.get(true)
                        .stream()
                        .findFirst()
                        .orElse(null);

                if(userDetails != null && redisToken != null) {
                    List<Token> remainingTokens = partitionedTokens.get(false);
                    if(!remainingTokens.isEmpty()) {
                        tokenService.deleteAll(remainingTokens);
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    authToken.getDetails();
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
