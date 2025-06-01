package org.edu.fpm.transportation.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.edu.fpm.transportation.dto.auth.AuthResponseDto;
import org.edu.fpm.transportation.dto.auth.signin.SignInRequestDto;
import org.edu.fpm.transportation.dto.auth.signup.SignUpRequestDto;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.repository.UserRepository;
import org.edu.fpm.transportation.service.UserService;
import org.edu.fpm.transportation.validation.UserValidation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserValidation userValidation;

    /**
     * Sign in a user and generate JWT token
     */
    public AuthResponseDto signIn(SignInRequestDto request) {
        log.info("Authenticating user: {}", request.getEmail());
        
        // Validate login request
        userValidation.validateLoginRequest(request);
        
        // Authenticate user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        // Get user from repository
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Generate JWT token
        String token = jwtService.generateToken(user);
        
        // Create response
        return AuthResponseDto.builder()
            .accessToken(token)
            .role(user.getRoleName())
            .userId(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .build();
    }

    /**
     * Register a new user and generate JWT token
     */
    public AuthResponseDto signUp(SignUpRequestDto request) {
        log.info("Registering new user: {}", request.getEmail());
        
        // Validate registration data
        userValidation.validateUserRegistration(request);
        
        // Register user
        User user = userService.registerUser(request);
        
        // Generate JWT token
        String token = jwtService.generateToken(user);
        
        // Create response
        return AuthResponseDto.builder()
            .accessToken(token)
            .role(user.getRoleName())
            .userId(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .build();
    }
}
