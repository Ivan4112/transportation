package org.edu.fpm.transportation.controller;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.order.OrderPriceResponseDto;
import org.edu.fpm.transportation.dto.order.OrderRequestDto;
import org.edu.fpm.transportation.dto.order.OrderResponseDto;
import org.edu.fpm.transportation.service.CustomerOrderService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.edu.fpm.transportation.util.RoleType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;
    private final JwtService jwtService;

    /**
     * Calculate price for a potential order
     */
    @PostMapping("/calculate-price")
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public OrderPriceResponseDto calculateOrderPrice(@RequestBody OrderRequestDto orderRequest) {
        return customerOrderService.calculateOrderPrice(orderRequest);
    }

    /**
     * Create a new order
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public OrderResponseDto createOrder(@RequestHeader("Authorization") String authHeader, 
                                        @RequestBody OrderRequestDto orderRequest) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer customerId = jwtService.getUserIdFromToken(token);
        
        return customerOrderService.createOrder(customerId, orderRequest);
    }

    /**
     * Get all orders for the authenticated customer
     */
    @GetMapping
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public List<OrderResponseDto> getCustomerOrders(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer customerId = jwtService.getUserIdFromToken(token);

        return customerOrderService.getCustomerOrders(customerId);
    }

    /**
     * Get a specific order for the authenticated customer
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public OrderResponseDto getCustomerOrder(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable Integer orderId) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer customerId = jwtService.getUserIdFromToken(token);
        
        return customerOrderService.getCustomerOrder(customerId, orderId);
    }
}
