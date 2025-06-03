package org.edu.fpm.transportation.controller.driver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.edu.fpm.transportation.dto.VehicleDto;
import org.edu.fpm.transportation.entity.Vehicle;
import org.edu.fpm.transportation.service.DriverService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for driver vehicle management operations
 */
@RestController
@RequestMapping("/api/driver/vehicle")
@RequiredArgsConstructor
@Slf4j
public class DriverVehicleController {

    private final DriverService driverService;
    private final JwtService jwtService;

    /**
     * Get information about the driver's vehicle
     */
    @GetMapping
    public ResponseEntity<VehicleDto> getDriverVehicle(@RequestHeader("Authorization") String authHeader) {
        try {
            log.info("Getting vehicle for driver from auth header: {}", authHeader);
            String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
            Integer userIdFromToken = jwtService.getUserIdFromToken(token);
            log.info("User ID from token: {}", userIdFromToken);

            Vehicle vehicle = driverService.getDriverVehicle(userIdFromToken);
            log.info("Found vehicle: {}", vehicle);
            return ResponseEntity.ok(VehicleDto.fromEntity(vehicle));
        } catch (Exception e) {
            log.error("Error getting driver vehicle: {}", e.getMessage(), e);
            throw e;
        }
    }
}
