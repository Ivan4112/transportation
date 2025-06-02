package org.edu.fpm.transportation.controller;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.OrderDto;
import org.edu.fpm.transportation.dto.order.OrderRequestDto;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.service.CustomerOrderService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getCustomerOrders(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        List<Order> orders = customerOrderService.getCustomerOrders(userIdFromToken);
        List<OrderDto> orderDtos = orders.stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDtos);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getCustomerOrderById(@PathVariable Integer orderId,
                                                      @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        Order order = customerOrderService.getCustomerOrderById(orderId, userIdFromToken);
        return ResponseEntity.ok(OrderDto.fromEntity(order));
    }

    @PostMapping("/create")
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderRequestDto orderRequest,
                                             @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userIdFromToken = jwtService.getUserIdFromToken(token);

        Order createdOrder = customerOrderService.createOrder(orderRequest, userIdFromToken);
        return ResponseEntity.ok(OrderDto.fromEntity(createdOrder));
    }
}
