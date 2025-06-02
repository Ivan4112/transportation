package org.edu.fpm.transportation.controller.driver;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.OrderStatusUpdateDto;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.Vehicle;
import org.edu.fpm.transportation.service.DriverService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final JwtService jwtService;
    /**
     * Get all orders assigned to the authenticated driver
     */
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAssignedOrders(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        List<Order> orders = driverService.getAssignedOrders(userIdFromToken);
        return ResponseEntity.ok(orders);
    }

    /**
     * Update the status of an order
     */
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Integer orderId,
                                                   @RequestBody OrderStatusUpdateDto statusUpdateDto,
                                                   @RequestHeader("Authorization") String authHeader) {

        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        Order updatedOrder = driverService.updateOrderStatus(orderId, statusUpdateDto, userIdFromToken);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Get information about the driver's vehicle
     */
    @GetMapping("/vehicle")
    public ResponseEntity<Vehicle> getDriverVehicle(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        Vehicle vehicle = driverService.getDriverVehicle(userIdFromToken);
        return ResponseEntity.ok(vehicle);
    }
}
