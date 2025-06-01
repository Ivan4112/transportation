package org.edu.fpm.transportation.controller;

import org.edu.fpm.transportation.dto.auth.AuthResponseDto;
import org.edu.fpm.transportation.dto.auth.signin.SignInRequestDto;
import org.edu.fpm.transportation.dto.auth.signup.SignUpRequestDto;
import org.edu.fpm.transportation.service.security.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto registerUser(@RequestBody SignUpRequestDto signUpRequestDto) {
        return authService.signUp(signUpRequestDto);
    }


    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto loginUser(@RequestBody SignInRequestDto signInRequestDto) {
        return authService.login(signInRequestDto);
    }
}
