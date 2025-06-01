package org.edu.fpm.transportation.controller;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.auth.AuthResponseDto;
import org.edu.fpm.transportation.dto.auth.signin.SignInRequestDto;
import org.edu.fpm.transportation.dto.auth.signup.SignUpRequestDto;
import org.edu.fpm.transportation.service.security.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponseDto> signIn(@RequestBody SignInRequestDto request) {
        return ResponseEntity.ok(authService.signIn(request));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDto> signUp(@RequestBody SignUpRequestDto request) {
        return ResponseEntity.ok(authService.signUp(request));
    }
}
