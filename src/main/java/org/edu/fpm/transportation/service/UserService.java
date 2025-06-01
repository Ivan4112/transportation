package org.edu.fpm.transportation.service;

import org.edu.fpm.transportation.dto.auth.signup.SignUpRequestDto;
import org.edu.fpm.transportation.entity.Role;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.repository.RoleRepository;
import org.edu.fpm.transportation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
        String roleName = "CUSTOMER"; // Default role
        Optional<Role> role = roleRepository.findByEmail(signUpRequestDto.getEmail());
        if (role.isPresent()) {
            roleName = role.get().getRoleName();
        }
        
        // Create new user
        User user = new User();
        user.setEmail(signUpRequestDto.getEmail());
        user.setPasswordHash(signUpRequestDto.getPassword());
        user.setFirstName(signUpRequestDto.getFirstName());
        user.setLastName(signUpRequestDto.getLastName());
        user.setRoleName(roleName);

        return userRepository.save(user);
    }

    public User updateUser(Integer id, User updatedUser) {
        User existingUser = getUserById(id);
        
        // Update fields
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        
        if (updatedUser.getPasswordHash() != null) {
            existingUser.setPasswordHash(updatedUser.getPasswordHash());
        }
        
        return userRepository.save(existingUser);
    }

    public User changeUserRole(Integer userId, String roleName) {
        User user = getUserById(userId);
        user.setRoleName(roleName);
        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
