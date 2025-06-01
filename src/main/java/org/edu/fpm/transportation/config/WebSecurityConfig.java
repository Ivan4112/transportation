package org.edu.fpm.transportation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuration for web security (HTML pages)
 * This configuration has higher precedence than the API security config
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] STATIC_RESOURCES = {
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**"
    };

    private static final String[] PUBLIC_URLS = {
            "/",
            "/login",
            "/register",
            "/error/**"
    };

    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/**"))
            .authorizeHttpRequests(auth -> auth
                // Allow access to static resources
                .requestMatchers(STATIC_RESOURCES).permitAll()
                // Allow access to public pages
                .requestMatchers(PUBLIC_URLS).permitAll()
                // Require authentication for customer pages
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                // Require authentication for all other pages
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
            
        return http.build();
    }
}
