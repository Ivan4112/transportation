package org.edu.fpm.transportation.config;

import lombok.extern.slf4j.Slf4j;
import org.edu.fpm.transportation.service.security.JwtAuthenticationFilter;
import org.edu.fpm.transportation.service.security.TokenParameterFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final TokenParameterFilter tokenParameterFilter;
    private final UserDetailsService userDetailsService;

    private static final String[] STATIC_RESOURCES = {
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**",
            "favicon.ico"
    };

    private static final String[] PUBLIC_URLS = {
            "/",
            "/login",
            "/register",
            "/error/**"
    };

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, 
                         TokenParameterFilter tokenParameterFilter,
                         UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.tokenParameterFilter = tokenParameterFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring API security filter chain");

        http
                .securityMatcher("/api/**") // Only apply to API endpoints
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    log.info("Configuring API authorization rules");
                    auth
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/v3/api-docs/**", "/api/swagger-ui/**").permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenParameterFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring web security filter chain");

        http
                .securityMatcher("/**") // Apply to all non-API endpoints
                .authorizeHttpRequests(auth -> {
                    log.info("Configuring web authorization rules");
                    auth
                            // Allow access to static resources
                            .requestMatchers(STATIC_RESOURCES).permitAll()
                            // Allow access to public pages
                            .requestMatchers(PUBLIC_URLS).permitAll()
                            // Allow access to Quick Links pages
                            .requestMatchers("/about", "/services", "/contact").permitAll()
                            // Require specific roles for certain pages
                            .requestMatchers("/customer/**").hasRole("CUSTOMER")
                            .requestMatchers("/driver/**").hasRole("DRIVER")
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            // Require authentication for all other requests
                            .anyRequest().authenticated();
                })
                .formLogin(form -> form.disable()) // Disable form login as we're using JWT
                .logout(logout -> logout.disable()) // Disable default logout as we're handling it via JavaScript
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenParameterFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true); // Allow credentials (cookies)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
