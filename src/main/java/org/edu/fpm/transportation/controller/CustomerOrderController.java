package org.edu.fpm.transportation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.edu.fpm.transportation.dto.OrderDto;
import org.edu.fpm.transportation.dto.order.OrderPriceResponseDto;
import org.edu.fpm.transportation.dto.order.OrderRequestDto;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.service.CustomerOrderService;
import org.edu.fpm.transportation.service.PricingService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
@Slf4j
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;
    private final JwtService jwtService;
    private final PricingService pricingService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getCustomerOrders(@RequestHeader("Authorization") String authHeader) {
        log.info("Received request to get customer orders with auth header: {}", authHeader);
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        log.info("Extracted token: {}", token);
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        log.info("User ID from token: {}", userIdFromToken);

        List<Order> orders = customerOrderService.getCustomerOrders(userIdFromToken);
        log.info("Found {} orders for user ID: {}", orders.size(), userIdFromToken);
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDtos);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getCustomerOrderById(@PathVariable Integer orderId,
                                                      @RequestHeader("Authorization") String authHeader) {
        log.info("Received request to get customer order by ID: {} with auth header: {}", orderId, authHeader);
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        log.info("Extracted token: {}", token);
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);
        log.info("User ID from token: {}", userIdFromToken);

        Order order = customerOrderService.getCustomerOrderById(orderId, userIdFromToken);
        log.info("Found order with ID: {} for user ID: {}", orderId, userIdFromToken);
        return ResponseEntity.ok(OrderDto.fromEntity(order));
    }

    @PostMapping()
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderRequestDto orderRequest,
                                             @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        Order createdOrder = customerOrderService.createOrder(orderRequest, userIdFromToken);
        return ResponseEntity.ok(OrderDto.fromEntity(createdOrder));
    }
    
    @PostMapping("/calculate-price")
    public ResponseEntity<OrderPriceResponseDto> calculatePrice(@RequestBody OrderRequestDto orderRequest) {
        // Calculate distance between locations
        BigDecimal distance = pricingService.calculateDistance(
                orderRequest.getStartLocation(), 
                orderRequest.getEndLocation()
        );
        
        // Calculate price based on distance, cargo weight and type
        BigDecimal price = pricingService.calculatePrice(
                distance,
                orderRequest.getCargoWeight(),
                orderRequest.getCargoType()
        );
        
        // Create and return response
        OrderPriceResponseDto response = new OrderPriceResponseDto(
                price,
                distance,
                orderRequest.getCargoType(),
                orderRequest.getCargoWeight(),
                orderRequest.getStartLocation(),
                orderRequest.getEndLocation()
        );
        
        return ResponseEntity.ok(response);
    }
}
