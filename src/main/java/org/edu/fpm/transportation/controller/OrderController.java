package org.edu.fpm.transportation.controller;

import org.edu.fpm.transportation.dto.OrderAssignmentDto;
import org.edu.fpm.transportation.dto.OrderCreateDto;
import org.edu.fpm.transportation.dto.OrderDto;
import org.edu.fpm.transportation.dto.OrderStatusUpdateDto;
import org.edu.fpm.transportation.entity.Cargo;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.OrderLocation;
import org.edu.fpm.transportation.entity.Route;
import org.edu.fpm.transportation.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(OrderDto.fromEntity(order));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/customer/{customerId}")
    public List<OrderDto> getOrdersByCustomer(@PathVariable Integer customerId) {
        return orderService.getOrdersByCustomerId(customerId).stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/driver/{driverId}")
    public List<OrderDto> getOrdersByDriver(@PathVariable Integer driverId) {
        return orderService.getOrdersByDriverId(driverId).stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    @PostMapping("/customer/{customerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@PathVariable Integer customerId, @RequestBody OrderCreateDto orderCreateDto) {
        Order order = orderService.createOrder(customerId, orderCreateDto);
        return OrderDto.fromEntity(order);
    }
    
    @PutMapping("/{orderId}/assign")
    public OrderDto assignDriverToOrder(@PathVariable Integer orderId, @RequestBody OrderAssignmentDto assignmentDto) {
        Order order = orderService.assignDriverToOrder(orderId, assignmentDto);
        return OrderDto.fromEntity(order);
    }
    
    @PutMapping("/{orderId}/status")
    public OrderDto updateOrderStatus(@PathVariable Integer orderId, @RequestBody OrderStatusUpdateDto statusUpdateDto) {
        Order order = orderService.updateOrderStatus(orderId, statusUpdateDto);
        return OrderDto.fromEntity(order);
    }
    
    @GetMapping("/{orderId}/location")
    public ResponseEntity<OrderLocation> getLatestOrderLocation(@PathVariable Integer orderId) {
        OrderLocation location = orderService.getLatestOrderLocation(orderId);
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{orderId}/location/history")
    public List<OrderLocation> getOrderLocationHistory(@PathVariable Integer orderId) {
        return orderService.getOrderLocationHistory(orderId);
    }
    
    @GetMapping("/{id}/route")
    public ResponseEntity<Route> getOrderRoute(@PathVariable Integer id) {
        Route route = orderService.getOrderRoute(id);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/{id}/cargo")
    public ResponseEntity<Cargo> getOrderCargo(@PathVariable Integer id) {
        Cargo cargo = orderService.getOrderCargo(id);
        return ResponseEntity.ok(cargo);
    }
}
