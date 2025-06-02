package org.edu.fpm.transportation.controller.support;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.service.SupportAgentService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for support agent order management operations
 */
@RestController
@RequestMapping("/api/support/orders")
@RequiredArgsConstructor
public class SupportOrderController {
    private final SupportAgentService supportAgentService;
    private final JwtService jwtService;
    
    /**
     * Get all orders in the system
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        List<Order> orders = supportAgentService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    /**
     * Assign a driver and vehicle to an order
     */
    @PutMapping("/{orderId}/assign")
    public ResponseEntity<Order> assignDriverToOrder(
            @PathVariable Integer orderId,
            @RequestParam(required = true) Integer driverId,
            @RequestParam(required = true) Integer vehicleId,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        Order updatedOrder = supportAgentService.assignDriverToOrder(orderId, driverId, vehicleId);
        return ResponseEntity.ok(updatedOrder);
    }
    
    /**
     * Update order status
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam(required = true) Integer statusId,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        Order updatedOrder = supportAgentService.updateOrderStatus(orderId, statusId);
        return ResponseEntity.ok(updatedOrder);
    }
    
    /**
     * Get all drivers in the system
     */
    @GetMapping("/drivers")
    public ResponseEntity<List<User>> getAllDrivers(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        List<User> drivers = supportAgentService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }
}
