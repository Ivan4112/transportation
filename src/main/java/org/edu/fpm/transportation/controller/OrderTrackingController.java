package org.edu.fpm.transportation.controller;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.entity.OrderLocation;
import org.edu.fpm.transportation.service.OrderService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class OrderTrackingController {

    private final OrderService orderService;
    private final JwtService jwtService;

    /**
     * Get the latest location for an order
     */
    @GetMapping("/orders/{orderId}/location")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderLocation> getLatestOrderLocation(@PathVariable Integer orderId) {
        OrderLocation location = orderService.getLatestOrderLocation(orderId);
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get location history for an order
     */
    @GetMapping("/orders/{orderId}/location/history")
    @PreAuthorize("isAuthenticated()")
    public List<OrderLocation> getOrderLocationHistory(@PathVariable Integer orderId) {
        return orderService.getOrderLocationHistory(orderId);
    }
}
