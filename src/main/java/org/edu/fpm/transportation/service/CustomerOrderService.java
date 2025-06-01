package org.edu.fpm.transportation.service;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.order.OrderPriceResponseDto;
import org.edu.fpm.transportation.dto.order.OrderRequestDto;
import org.edu.fpm.transportation.dto.order.OrderResponseDto;
import org.edu.fpm.transportation.entity.*;
import org.edu.fpm.transportation.exception.NotFoundException;
import org.edu.fpm.transportation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.edu.fpm.transportation.util.ResourceConstants.NOT_FOUND_ORDER_STATUS;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final RouteRepository routeRepository;
    private final CargoRepository cargoRepository;
    private final PricingService pricingService;
    private final NotificationService notificationService;

    /**
     * Calculate price for a potential order
     */
    public OrderPriceResponseDto calculateOrderPrice(OrderRequestDto orderRequest) {
        // Calculate distance
        BigDecimal distance = pricingService.calculateDistance(
                orderRequest.getStartLocation(), 
                orderRequest.getEndLocation()
        );
        
        // Calculate price
        BigDecimal price = pricingService.calculatePrice(
                distance,
                orderRequest.getCargoWeight(),
                orderRequest.getCargoType()
        );
        
        // Create response
        OrderPriceResponseDto response = new OrderPriceResponseDto();
        response.setPrice(price);
        response.setDistance(distance);
        response.setCargoType(orderRequest.getCargoType());
        response.setCargoWeight(orderRequest.getCargoWeight());
        response.setStartLocation(orderRequest.getStartLocation());
        response.setEndLocation(orderRequest.getEndLocation());
        
        return response;
    }
    
    /**
     * Create a new order for a customer
     */
    @Transactional
    public OrderResponseDto createOrder(Integer customerId, OrderRequestDto orderRequest) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + customerId));
        
        // Calculate distance and price
        BigDecimal distance = pricingService.calculateDistance(
                orderRequest.getStartLocation(), 
                orderRequest.getEndLocation()
        );
        
        BigDecimal price = pricingService.calculatePrice(
                distance,
                orderRequest.getCargoWeight(),
                orderRequest.getCargoType()
        );
        
        // Get initial status (PENDING)
        Optional<OrderStatus> initialStatus = orderStatusRepository.findByStatusName("PENDING");

        if(initialStatus.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_ORDER_STATUS);
        }
        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(initialStatus.get());
        order.setPrice(price);
        order.setCreatedAt(Instant.now());
        
        Order savedOrder = orderRepository.save(order);
        
        // Create route
        Route route = new Route();
        route.setOrder(savedOrder);
        route.setStartLocation(orderRequest.getStartLocation());
        route.setEndLocation(orderRequest.getEndLocation());
        route.setDistance(distance);
        
        // Estimate delivery time (simplified: 1 hour per 100km)
        double hours = distance.doubleValue() / 100.0;
        route.setEstimatedTime(Instant.now().plus((long)(hours * 60), ChronoUnit.MINUTES));
        
        routeRepository.save(route);
        
        // Create cargo
        Cargo cargo = new Cargo();
        cargo.setOrder(savedOrder);
        cargo.setType(orderRequest.getCargoType());
        cargo.setWeight(orderRequest.getCargoWeight());
        
        cargoRepository.save(cargo);
        
        // Create notification for order creation
        notificationService.createOrderStatusNotification(
                savedOrder.getId(), 
                "Your order has been created and is pending assignment."
        );
        
        return convertToResponseDto(savedOrder, cargo, route);
    }
    
    /**
     * Get all orders for a customer
     */
    public List<OrderResponseDto> getCustomerOrders(Integer customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + customerId));
        
        List<Order> orders = orderRepository.findByCustomer(customer);
        List<OrderResponseDto> responseDtos = new ArrayList<>();
        
        for (Order order : orders) {
            Optional<Cargo> cargoOpt = cargoRepository.findByOrder(order);
            Optional<Route> routeOpt = routeRepository.findByOrder(order);
            
            if (cargoOpt.isPresent() && routeOpt.isPresent()) {
                responseDtos.add(convertToResponseDto(order, cargoOpt.get(), routeOpt.get()));
            }
        }
        
        return responseDtos;
    }
    
    /**
     * Get a specific order for a customer
     */
    public OrderResponseDto getCustomerOrder(Integer customerId, Integer orderId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + customerId));
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        
        // Verify that the order belongs to the customer
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Order does not belong to the specified customer");
        }
        
        Cargo cargo = cargoRepository.findByOrder(order)
                .orElseThrow(() -> new NoSuchElementException("Cargo not found for order: " + orderId));
        
        Route route = routeRepository.findByOrder(order)
                .orElseThrow(() -> new NoSuchElementException("Route not found for order: " + orderId));
        
        return convertToResponseDto(order, cargo, route);
    }
    
    /**
     * Convert entities to response DTO
     */
    private OrderResponseDto convertToResponseDto(Order order, Cargo cargo, Route route) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        
        dto.setCustomerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        
        if (order.getDriver() != null) {
            dto.setDriverName(order.getDriver().getFirstName() + " " + order.getDriver().getLastName());
        }
        
        if (order.getVehicle() != null) {
            dto.setVehicleLicensePlate(order.getVehicle().getLicensePlate());
        }
        
        dto.setStatus(order.getStatus().getStatusName());
        dto.setPrice(order.getPrice());
        dto.setCargoType(cargo.getType());
        dto.setCargoWeight(cargo.getWeight());
        dto.setStartLocation(route.getStartLocation());
        dto.setEndLocation(route.getEndLocation());
        dto.setDistance(route.getDistance());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setEstimatedDeliveryTime(route.getEstimatedTime());
        
        return dto;
    }
}
