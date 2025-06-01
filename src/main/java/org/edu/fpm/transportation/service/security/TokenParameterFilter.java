package org.edu.fpm.transportation.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Filter to handle JWT token from URL parameter
 * This filter runs before JwtAuthenticationFilter to ensure token is available
 */
@Component
@Order(1) // Run before JwtAuthenticationFilter
@Slf4j
public class TokenParameterFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Check if token is in URL parameter
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            log.info("Found token in URL parameter, adding to request headers and cookies");
            
            // Create request wrapper with added Authorization header
            HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if ("Authorization".equalsIgnoreCase(name)) {
                        return "Bearer " + tokenParam;
                    }
                    return super.getHeader(name);
                }
                
                @Override
                public Enumeration<String> getHeaderNames() {
                    List<String> names = Collections.list(super.getHeaderNames());
                    if (!names.contains("Authorization")) {
                        names.add("Authorization");
                    }
                    return Collections.enumeration(names);
                }
            };
            
            // Add token as cookie for future requests
            Cookie cookie = new Cookie("jwt_token", tokenParam);
            cookie.setPath("/");
            cookie.setMaxAge(86400);
            response.addCookie(cookie);
            
            // Continue with wrapped request
            filterChain.doFilter(requestWrapper, response);
        } else {
            // Continue without changes
            filterChain.doFilter(request, response);
        }
    }
}
