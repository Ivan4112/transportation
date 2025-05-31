package org.edu.fpm.transportation.controller;

import org.edu.fpm.transportation.dto.OrderAssignmentDto;
import org.edu.fpm.transportation.dto.OrderCreateDto;
import org.edu.fpm.transportation.dto.OrderStatusUpdateDto;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.OrderLocation;
import org.edu.fpm.transportation.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersByCustomer(@PathVariable Integer customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }
    
    @GetMapping("/driver/{driverId}")
    public List<Order> getOrdersByDriver(@PathVariable Integer driverId) {
        return orderService.getOrdersByDriverId(driverId);
    }
    
    @PostMapping("/customer/{customerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@PathVariable Integer customerId, @RequestBody OrderCreateDto orderCreateDto) {
        return orderService.createOrder(customerId, orderCreateDto);
    }
    
    @PutMapping("/{orderId}/assign")
    public Order assignDriverToOrder(@PathVariable Integer orderId, @RequestBody OrderAssignmentDto assignmentDto) {
        return orderService.assignDriverToOrder(orderId, assignmentDto);
    }
    
    @PutMapping("/{orderId}/status")
    public Order updateOrderStatus(@PathVariable Integer orderId, @RequestBody OrderStatusUpdateDto statusUpdateDto) {
        return orderService.updateOrderStatus(orderId, statusUpdateDto);
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
}
