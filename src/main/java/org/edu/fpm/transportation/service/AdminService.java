package org.edu.fpm.transportation.service;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.entity.Role;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.exception.AccessDeniedException;
import org.edu.fpm.transportation.exception.ResourceNotFoundException;
import org.edu.fpm.transportation.repository.RoleRepository;
import org.edu.fpm.transportation.repository.UserRepository;
import org.edu.fpm.transportation.util.RoleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for admin operations
 */
@Service
@RequiredArgsConstructor
public class AdminService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Assign a role to a user by email
     * 
     * @param email User email
     * @param roleName Role name to assign
     * @return The created or updated role assignment
     */
    @Transactional
    public Role assignRoleToUser(String email, String roleName) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        
        // Validate that the role name is valid
        try {
            RoleType.fromString(roleName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role name: " + roleName);
        }
        
        // Check if the user exists
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        // Check if there's already a role assignment for this email
        Optional<Role> existingRole = roleRepository.findByEmail(email);
        
        Role role;
        if (existingRole.isPresent()) {
            // Update existing role
            role = existingRole.get();
            role.setRoleName(roleName);
        } else {
            // Create new role assignment
            role = new Role();
            role.setEmail(email);
            role.setRoleName(roleName);
        }
        
        // Save the role assignment
        Role savedRole = roleRepository.save(role);
        
        // If the user already exists, update their role
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRoleName(roleName);
            userRepository.save(user);
        }
        
        return savedRole;
    }
    
    /**
     * Remove a role assignment for a user by email
     * 
     * @param email User email
     */
    @Transactional
    public void removeRoleFromUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        
        // Find the role assignment
        Role role = roleRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No role assignment found for email: " + email));
        
        // Delete the role assignment
        roleRepository.delete(role);
        
        // If the user exists, set their role to CUSTOMER (default)
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRoleName(RoleType.CUSTOMER.getRoleName());
            userRepository.save(user);
        }
    }
    
    /**
     * Get all role assignments
     * 
     * @return List of all role assignments
     */
    public List<Role> getAllRoleAssignments() {
        return roleRepository.findAll();
    }
    
    /**
     * Validate that the user has the ADMIN role
     * 
     * @param userId User ID to validate
     * @throws AccessDeniedException if the user doesn't have the ADMIN role
     */
    public void validateAdminRole(Integer userId) {
        User user = userService.getUserById(userId);
        if (!RoleType.Constants.ADMIN.equals(user.getRoleName())) {
            throw new AccessDeniedException("This operation requires ADMIN role");
        }
    }
}
