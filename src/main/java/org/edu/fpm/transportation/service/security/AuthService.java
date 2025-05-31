package org.edu.fpm.transportation.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.edu.fpm.transportation.dto.auth.AuthResponseDto;
import org.edu.fpm.transportation.dto.auth.signin.SignInRequestDto;
import org.edu.fpm.transportation.dto.auth.signup.SignUpRequestDto;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.exception.InvalidCredentialsForAuthException;
import org.edu.fpm.transportation.repository.UserRepository;
import org.edu.fpm.transportation.service.UserService;
import org.edu.fpm.transportation.validation.UserValidation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.edu.fpm.transportation.util.ResourceConstants.MESSAGE_CREDENTIALS_INVALID;


/**
 * Service class for authentication operations.
 * Handles user registration, authentication, and token management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserValidation userValidation;

    public AuthResponseDto signUp(SignUpRequestDto request) {
        userValidation.validateUserRegistration(request);

        User user = userService.registerUser(request);

        String accessToken = jwtService.generateToken(user);

        return new AuthResponseDto(
                accessToken,
                user.getRoleName(),
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    public AuthResponseDto login(SignInRequestDto request) {
        userValidation.validateLoginRequest(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsForAuthException(MESSAGE_CREDENTIALS_INVALID));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsForAuthException(MESSAGE_CREDENTIALS_INVALID);
        }

        String accessToken = jwtService.generateToken(user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .role(user.getRoleName())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    /*public boolean deleteUserByEmail(String userEmail) {
        log.info("Starting coordinated user deletion process for: {}", userEmail);

        User deletedUser = userService.deleteUserByEmail(userEmail);
        return deletedUser != null;
    }*/

    public String extractTokenFromHeaders(Map<String, String> headers) {
        if (headers == null) {
            log.info("Headers are null");
            return null;
        }

        String authorizationHeader = headers.get("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("Authorization header is missing or invalid");
            return null;
        }

        String token = authorizationHeader.substring(7);
        log.info("Token extracted successfully");
        return token;
    }

    public boolean isAuthorized(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.info("Token is null or empty");
            return false;
        }

        try {
            String email = jwtService.extractUsername(token);
            User user = userRepository.findByEmail(email).orElse(null);
            return user != null && jwtService.isTokenValid(token, user);
        } catch (Exception e) {
            log.error("Error validating token", e);
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        try {
            return jwtService.getUserIdFromToken(token);
        } catch (Exception e) {
            log.error("Error getting userId from token", e);
            return null;
        }
    }

}
