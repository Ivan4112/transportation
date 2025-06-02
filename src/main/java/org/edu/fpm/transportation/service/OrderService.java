package org.edu.fpm.transportation.service;

import org.edu.fpm.transportation.dto.OrderAssignmentDto;
import org.edu.fpm.transportation.dto.OrderCreateDto;
import org.edu.fpm.transportation.dto.OrderStatusUpdateDto;
import org.edu.fpm.transportation.entity.*;
import org.edu.fpm.transportation.exception.ResourceNotFoundException;
import org.edu.fpm.transportation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final RouteRepository routeRepository;
    private final OrderLocationRepository orderLocationRepository;
    private final CargoRepository cargoRepository;

    public OrderService(OrderRepository orderRepository, 
                       UserRepository userRepository,
                       VehicleRepository vehicleRepository,
                       OrderStatusRepository orderStatusRepository,
                       RouteRepository routeRepository,
                       OrderLocationRepository orderLocationRepository,
                       CargoRepository cargoRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.routeRepository = routeRepository;
        this.orderLocationRepository = orderLocationRepository;
        this.cargoRepository = cargoRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
    
    public List<Order> getOrdersByCustomerId(Integer customerId) {
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + customerId));
        return orderRepository.findByCustomer(customer);
    }
    
    public List<Order> getOrdersByDriverId(Integer driverId) {
        User driver = userRepository.findById(driverId)
            .orElseThrow(() -> new NoSuchElementException("Driver not found with id: " + driverId));
        return orderRepository.findByDriver(driver);
    }
    
    @Transactional
    public Order createOrder(Integer customerId, OrderCreateDto orderCreateDto) {
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + customerId));
        
        // Create new order
        Order order = new Order();
        order.setCustomer(customer);
        order.setCreatedAt(Instant.now());
        
        // Set initial status (e.g., "Pending")
        OrderStatus initialStatus = orderStatusRepository.findById(1) // Assuming 1 is "Pending"
            .orElseThrow(() -> new NoSuchElementException("Initial order status not found"));
        order.setStatus(initialStatus);
        
        Order savedOrder = orderRepository.save(order);
        
        // Create route for the order
        Route route = new Route();
        route.setOrder(savedOrder);
        route.setStartLocation(orderCreateDto.getStartLocation());
        route.setEndLocation(orderCreateDto.getEndLocation());
        routeRepository.save(route);
        
        // Create cargo information
        Cargo cargo = new Cargo();
        cargo.setOrder(savedOrder);
        cargo.setType(orderCreateDto.getCargoType());
        cargo.setWeight(orderCreateDto.getWeight());
        cargoRepository.save(cargo);
        
        return savedOrder;
    }
    
    @Transactional
    public Order assignDriverToOrder(Integer orderId, OrderAssignmentDto assignmentDto) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        
        User driver = userRepository.findById(assignmentDto.getDriverId())
            .orElseThrow(() -> new NoSuchElementException("Driver not found with id: " + assignmentDto.getDriverId()));
        
        Vehicle vehicle = vehicleRepository.findById(assignmentDto.getVehicleId())
            .orElseThrow(() -> new NoSuchElementException("Vehicle not found with id: " + assignmentDto.getVehicleId()));
        
        // Verify that the vehicle belongs to the driver
        if (!vehicle.getDriver().getId().equals(driver.getId())) {
            throw new IllegalArgumentException("Vehicle does not belong to the specified driver");
        }
        
        order.setDriver(driver);
        order.setVehicle(vehicle);
        
        // Update status to "Assigned"
        OrderStatus assignedStatus = orderStatusRepository.findById(2) // Assuming 2 is "Assigned"
            .orElseThrow(() -> new NoSuchElementException("Assigned order status not found"));
        order.setStatus(assignedStatus);
        
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order updateOrderStatus(Integer orderId, OrderStatusUpdateDto statusUpdateDto) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        
        OrderStatus newStatus = orderStatusRepository.findById(statusUpdateDto.getStatusId())
            .orElseThrow(() -> new NoSuchElementException("Order status not found with id: " + statusUpdateDto.getStatusId()));
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        
        // If location data is provided, save it
        if (statusUpdateDto.getLatitude() != null && statusUpdateDto.getLongitude() != null) {
            OrderLocation location = new OrderLocation();
            location.setOrder(updatedOrder);
            location.setLatitude(statusUpdateDto.getLatitude());
            location.setLongitude(statusUpdateDto.getLongitude());
            location.setTimestamp(Instant.now());
            orderLocationRepository.save(location);
        }
        
        return updatedOrder;
    }
    
    public OrderLocation getLatestOrderLocation(Integer orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        return orderLocationRepository.findFirstByOrderOrderByTimestampDesc(order)
            .orElse(null);
    }
    
    public List<OrderLocation> getOrderLocationHistory(Integer orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        return orderLocationRepository.findByOrderOrderByTimestampDesc(order);
    }
    
    public Route getOrderRoute(Integer orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        return routeRepository.findByOrder(order)
            .orElseThrow(() -> new ResourceNotFoundException("Route not found for order: " + orderId));
    }
    
    public Cargo getOrderCargo(Integer orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        return cargoRepository.findByOrder(order)
            .orElseThrow(() -> new ResourceNotFoundException("Cargo not found for order: " + orderId));
    }
}
