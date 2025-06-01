package org.edu.fpm.transportation.controller;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.auth.signup.SignUpRequestDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for authentication-related views
 */
@Controller
@RequiredArgsConstructor
public class AuthViewController {

    /**
     * Login page
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, 
                       @RequestParam(required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        
        return "login";
    }

    /**
     * Registration page
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("signUpRequest", new SignUpRequestDto());
        return "register";
    }
}
