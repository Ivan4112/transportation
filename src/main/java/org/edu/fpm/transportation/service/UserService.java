package org.edu.fpm.transportation.service;

import lombok.extern.slf4j.Slf4j;
import org.edu.fpm.transportation.dto.auth.signup.SignUpRequestDto;
import org.edu.fpm.transportation.entity.Role;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.repository.RoleRepository;
import org.edu.fpm.transportation.repository.UserRepository;
import org.edu.fpm.transportation.util.RoleType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    @Transactional
    public User registerUser(SignUpRequestDto signUpRequestDto) {
        // Check if user already exists
        if (userRepository.findByEmail(signUpRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        
        // Determine role based on email
        RoleType roleType = RoleType.CUSTOMER; // Default role
        Optional<Role> role = roleRepository.findByEmail(signUpRequestDto.getEmail());
        if (role.isPresent()) {
            roleType = RoleType.fromString(role.get().getRoleName());
        }
        
        // Create new user
        User user = new User();
        user.setEmail(signUpRequestDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signUpRequestDto.getPassword()));
        user.setFirstName(signUpRequestDto.getFirstName());
        user.setLastName(signUpRequestDto.getLastName());
        user.setRoleName(roleType.getRoleName());
        
        return userRepository.save(user);
    }

    public User updateUser(Integer id, User updatedUser) {
        User existingUser = getUserById(id);
        
        // Update fields
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        
        if (updatedUser.getPasswordHash() != null) {
            existingUser.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
        }
        
        return userRepository.save(existingUser);
    }

    public User changeUserRole(Integer userId, RoleType roleType) {
        User user = getUserById(userId);
        user.setRoleName(roleType.getRoleName());
        return userRepository.save(user);
    }
    
    public User changeUserRole(Integer userId, String roleName) {
        RoleType roleType = RoleType.fromString(roleName);
        return changeUserRole(userId, roleType);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
