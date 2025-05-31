package org.edu.fpm.transportation.service;

import org.edu.fpm.transportation.entity.Role;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.repository.RoleRepository;
import org.edu.fpm.transportation.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));
    }

    public List<User> getUsersByRole(Integer roleId) {
        return userRepository.findByRoleId(roleId);
    }

    public User createUser(User user) {
        // Additional validation could be added here
        return userRepository.save(user);
    }

    public User updateUser(Integer id, User updatedUser) {
        User existingUser = getUserById(id);
        
        // Update fields
        existingUser.setUsername(updatedUser.getUsername());
        if (updatedUser.getPasswordHash() != null) {
            existingUser.setPasswordHash(updatedUser.getPasswordHash());
        }
        
        return userRepository.save(existingUser);
    }

    public User changeUserRole(Integer userId, Integer roleId) {
        User user = getUserById(userId);
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new NoSuchElementException("Role not found with id: " + roleId));
        
        user.setRole(role);
        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
