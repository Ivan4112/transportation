package org.edu.fpm.transportation.controller.support;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.entity.Vehicle;
import org.edu.fpm.transportation.service.SupportAgentService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for support agent vehicle management operations
 */
@RestController
@RequestMapping("/api/support/vehicles")
@RequiredArgsConstructor
public class SupportVehicleController {
    private final SupportAgentService supportAgentService;
    private final JwtService jwtService;
    
    /**
     * Get all vehicles in the system
     */
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        List<Vehicle> vehicles = supportAgentService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }
    
    /**
     * Get a specific vehicle by ID
     */
    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleById(
            @PathVariable Integer vehicleId,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        
        // Find vehicle by ID
        Vehicle vehicle = supportAgentService.getAllVehicles().stream()
                .filter(v -> v.getId().equals(vehicleId))
                .findFirst()
                .orElseThrow(() -> new org.edu.fpm.transportation.exception.ResourceNotFoundException("Vehicle not found with id: " + vehicleId));
        
        return ResponseEntity.ok(vehicle);
    }
    
    /**
     * Create a new vehicle
     */
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(
            @RequestBody Vehicle vehicle,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        Vehicle createdVehicle = supportAgentService.createVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
    }
    
    /**
     * Update an existing vehicle
     */
    @PutMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable Integer vehicleId,
            @RequestBody Vehicle vehicle,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        Vehicle updatedVehicle = supportAgentService.updateVehicle(vehicleId, vehicle);
        return ResponseEntity.ok(updatedVehicle);
    }
    
    /**
     * Delete a vehicle
     */
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Integer vehicleId,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        supportAgentService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }
}
