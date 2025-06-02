package org.edu.fpm.transportation.controller.support;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.VehicleDto;
import org.edu.fpm.transportation.entity.Vehicle;
import org.edu.fpm.transportation.service.SupportAgentService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<VehicleDto>> getAllVehicles(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        List<Vehicle> vehicles = supportAgentService.getAllVehicles();
        List<VehicleDto> vehicleDtos = vehicles.stream()
                .map(VehicleDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(vehicleDtos);
    }
    
    /**
     * Get a specific vehicle by ID
     */
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDto> getVehicleById(
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
        
        return ResponseEntity.ok(VehicleDto.fromEntity(vehicle));
    }
    
    /**
     * Create a new vehicle
     */
    @PostMapping
    public ResponseEntity<VehicleDto> createVehicle(
            @RequestBody Vehicle vehicle,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        Vehicle createdVehicle = supportAgentService.createVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(VehicleDto.fromEntity(createdVehicle));
    }
    
    /**
     * Update an existing vehicle
     */
    @PutMapping("/{vehicleId}")
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Integer vehicleId,
            @RequestBody Vehicle vehicle,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        Vehicle updatedVehicle = supportAgentService.updateVehicle(vehicleId, vehicle);
        return ResponseEntity.ok(VehicleDto.fromEntity(updatedVehicle));
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
