package org.edu.fpm.transportation.controller.support;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.OrderDto;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.service.SupportAgentService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<OrderDto>> getAllOrders(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        List<Order> orders = supportAgentService.getAllOrders();
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDtos);
    }
    
    /**
     * Assign a driver and vehicle to an order
     */
    @PutMapping("/{orderId}/assign")
    public ResponseEntity<OrderDto> assignDriverToOrder(
            @PathVariable Integer orderId,
            @RequestParam(required = true) Integer driverId,
            @RequestParam(required = true) Integer vehicleId,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        Order updatedOrder = supportAgentService.assignDriverToOrder(orderId, driverId, vehicleId);
        return ResponseEntity.ok(OrderDto.fromEntity(updatedOrder));
    }
    
    /**
     * Update order status
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam(required = true) Integer statusId,
            @RequestHeader("Authorization") String authHeader) {
        
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        Order updatedOrder = supportAgentService.updateOrderStatus(orderId, statusId);
        return ResponseEntity.ok(OrderDto.fromEntity(updatedOrder));
    }
    
    /**
     * Get all drivers in the system
     */
    @GetMapping("/drivers")
    public ResponseEntity<List<OrderDto.UserDto>> getAllDrivers(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        
        supportAgentService.validateSupportAgentRole(userIdFromToken);
        List<User> drivers = supportAgentService.getAllDrivers();
        List<OrderDto.UserDto> driverDtos = drivers.stream()
                .map(OrderDto.UserDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(driverDtos);
    }
}
