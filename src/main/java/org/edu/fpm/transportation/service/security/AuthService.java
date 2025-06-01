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

}
