package org.edu.fpm.transportation.controller.driver;

import lombok.RequiredArgsConstructor;
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
public class DriverVehicleController {

    private final DriverService driverService;
    private final JwtService jwtService;

    /**
     * Get information about the driver's vehicle
     */
    @GetMapping
    public ResponseEntity<Vehicle> getDriverVehicle(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        Vehicle vehicle = driverService.getDriverVehicle(userIdFromToken);
        return ResponseEntity.ok(vehicle);
    }
}
