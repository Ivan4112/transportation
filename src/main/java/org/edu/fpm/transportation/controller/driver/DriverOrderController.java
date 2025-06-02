package org.edu.fpm.transportation.controller.driver;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.DriverOrderStatusDto;
import org.edu.fpm.transportation.dto.OrderDto;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.OrderLocation;
import org.edu.fpm.transportation.entity.OrderStatus;
import org.edu.fpm.transportation.exception.ResourceNotFoundException;
import org.edu.fpm.transportation.service.DriverService;
import org.edu.fpm.transportation.service.OrderService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for driver order management operations
 */
@RestController
@RequestMapping("/api/driver/orders")
@RequiredArgsConstructor
public class DriverOrderController {

    private final DriverService driverService;
    private final OrderService orderService;
    private final JwtService jwtService;

    /**
     * Get all orders assigned to the authenticated driver
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAssignedOrders(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        List<Order> orders = driverService.getAssignedOrders(userIdFromToken);
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDtos);
    }

    /**
     * Get a specific order by ID (only if assigned to the driver)
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Integer orderId,
                                              @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        Order order = driverService.getDriverOrderById(orderId, userIdFromToken);
        return ResponseEntity.ok(OrderDto.fromEntity(order));
    }

    /**
     * Update the status of an order
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody DriverOrderStatusDto statusUpdateDto,
            @RequestHeader("Authorization") String authHeader) {

        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        Order updatedOrder = driverService.updateOrderStatus(orderId, statusUpdateDto, userIdFromToken);
        return ResponseEntity.ok(OrderDto.fromEntity(updatedOrder));
    }

    /**
     * Get the latest location of an order
     */
    @GetMapping("/{orderId}/location")
    public ResponseEntity<OrderLocation> getLatestOrderLocation(
            @PathVariable Integer orderId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Verify the driver has access to this order
            String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
            Integer userIdFromToken = jwtService.getUserIdFromToken(token);
            
            // This will throw ResourceNotFoundException if order doesn't exist
            driverService.getDriverOrderById(orderId, userIdFromToken);
            
            OrderLocation location = orderService.getLatestOrderLocation(orderId);
            if (location != null) {
                return ResponseEntity.ok(location);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ResourceNotFoundException e) {
            // Let the GlobalExceptionHandler handle this exception
            throw e;
        }
    }

    /**
     * Get all available order statuses that a driver can set
     */
    @GetMapping("/statuses")
    public ResponseEntity<List<OrderStatus>> getAvailableOrderStatuses() {
        List<OrderStatus> statuses = driverService.getDriverAvailableOrderStatuses();
        return ResponseEntity.ok(statuses);
    }
}
