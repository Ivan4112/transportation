package org.edu.fpm.transportation.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        log.info("Processing request for path: {}", path);
        
        // Log all headers for debugging
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("Header: {} = {}", headerName, request.getHeader(headerName));
        }
        
        // Log all cookies for debugging
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("Cookie: {} = {}", cookie.getName(), cookie.getValue());
            }
        } else {
            log.info("No cookies found in request");
        }
        
        if (isPublicEndpoint(path)) {
            log.debug("Public endpoint detected, skipping JWT validation");
            filterChain.doFilter(request, response);
            return;
        }

        // First check Authorization header
        String jwt = null;
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            log.debug("Found JWT token in Authorization header: {}", jwt);
        }
        
        // If token not found in header, check URL parameter
        if (jwt == null) {
            String tokenParam = request.getParameter("token");
            if (tokenParam != null && !tokenParam.isEmpty()) {
                jwt = tokenParam;
                log.debug("Found JWT token in URL parameter: {}", jwt);
                
                // Add token as cookie for future requests
                Cookie tokenCookie = new Cookie("jwt_token", jwt);
                tokenCookie.setPath("/");
                tokenCookie.setMaxAge(86400);
                response.addCookie(tokenCookie);
            }
        }
        
        // If token not found in header or URL, check cookies
        if (jwt == null) {
            Cookie[] requestCookies = request.getCookies();
            if (requestCookies != null) {
                for (Cookie cookie : requestCookies) {
                    if ("jwt_token".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        log.debug("Found JWT token in cookie: {}", jwt);
                        break;
                    }
                }
            }
        }
        
        if (jwt == null) {
            log.debug("No JWT token found");
            filterChain.doFilter(request, response);
            return;
        }
        
        // Process token
        try {
            String userEmail = jwtService.extractUsername(jwt);
            log.debug("Extracted email from JWT: {}", userEmail);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<User> userOptional = userRepository.findByEmail(userEmail);
                
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    log.debug("Found user: {}, role: {}", user.getEmail(), user.getRoleName());
                    
                    if (jwtService.isTokenValid(jwt, user)) {
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + user.getRoleName())
                        );
                        
                        log.debug("Granted authorities: {}", authorities);
                        
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                user.getEmail(),
                                null,
                                authorities
                        );
                        
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Authentication set in SecurityContext for user: {}", user.getEmail());
                    } else {
                        log.warn("Invalid JWT token for user: {}", user.getEmail());
                    }
                } else {
                    log.warn("User not found for email: {}", userEmail);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Check if the path is a public endpoint that doesn't require authentication
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/") ||
               path.equals("/login") ||
               path.equals("/register") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/error/") ||
               path.equals("/about") ||
               path.equals("/services") ||
               path.equals("/favicon.png") ||
               path.equals("/contact");
    }
}
