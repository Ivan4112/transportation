package org.edu.fpm.transportation.service;

import org.edu.fpm.transportation.dto.UserRegistrationDto;
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

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
    }

    public List<User> getUsersByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        
        // Determine role based on email
        String roleName = "CUSTOMER"; // Default role
        Optional<Role> role = roleRepository.findByEmail(registrationDto.getEmail());
        if (role.isPresent()) {
            roleName = role.get().getRoleName();
        }
        
        // Create new user
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(registrationDto.getPassword()); // In a real app, this should be hashed
        user.setFirstname(registrationDto.getFirstname());
        user.setLastname(registrationDto.getLastname());
        user.setRoleName(roleName);
        
        return userRepository.save(user);
    }

    public User updateUser(Integer id, User updatedUser) {
        User existingUser = getUserById(id);
        
        // Update fields
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFirstname(updatedUser.getFirstname());
        existingUser.setLastname(updatedUser.getLastname());
        
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
