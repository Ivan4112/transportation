package org.edu.fpm.transportation.controller.admin;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.admin.UserRoleAssignmentDto;
import org.edu.fpm.transportation.entity.Role;
import org.edu.fpm.transportation.service.AdminService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for admin user management operations
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminService adminService;
    private final JwtService jwtService;
    
    /**
     * Get all role assignments
     */
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoleAssignments(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        adminService.validateAdminRole(userIdFromToken);
        List<Role> roles = adminService.getAllRoleAssignments();
        return ResponseEntity.ok(roles);
    }
    
    /**
     * Assign a role to a user by email
     */
    @PostMapping("/roles")
    public ResponseEntity<Role> assignRoleToUser(
            @RequestBody UserRoleAssignmentDto assignmentDto,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        adminService.validateAdminRole(userIdFromToken);
        Role role = adminService.assignRoleToUser(assignmentDto.getEmail(), assignmentDto.getRoleName());
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }
    
    /**
     * Remove a role assignment for a user by email
     */
    @DeleteMapping("/roles")
    public ResponseEntity<Void> removeRoleFromUser(
            @RequestParam String email,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        adminService.validateAdminRole(userIdFromToken);
        adminService.removeRoleFromUser(email);
        return ResponseEntity.noContent().build();
    }
}
