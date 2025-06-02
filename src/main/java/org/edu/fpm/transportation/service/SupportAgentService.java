package org.edu.fpm.transportation.service;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.OrderStatus;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.entity.Vehicle;
import org.edu.fpm.transportation.exception.AccessDeniedException;
import org.edu.fpm.transportation.exception.ResourceNotFoundException;
import org.edu.fpm.transportation.repository.OrderRepository;
import org.edu.fpm.transportation.repository.OrderStatusRepository;
import org.edu.fpm.transportation.repository.UserRepository;
import org.edu.fpm.transportation.repository.VehicleRepository;
import org.edu.fpm.transportation.util.OrderStatusType;
import org.edu.fpm.transportation.util.RoleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportAgentService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final UserService userService;

    /**
     * Get all orders in the system
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Assign a driver and vehicle to an order
     */
    @Transactional
    public Order assignDriverToOrder(Integer orderId, Integer driverId, Integer vehicleId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (driverId == null) {
            throw new IllegalArgumentException("Driver ID cannot be null");
        }
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID cannot be null");
        }
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));
        
        // Verify that the user has DRIVER role
        if (!RoleType.Constants.DRIVER.equals(driver.getRoleName())) {
            throw new IllegalArgumentException("Selected user is not a driver");
        }
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));
        
        // Verify that the vehicle belongs to the driver
        if (!vehicle.getDriver().getId().equals(driver.getId())) {
            throw new IllegalArgumentException("Vehicle does not belong to the selected driver");
        }
        
        // Update order with driver and vehicle
        order.setDriver(driver);
        order.setVehicle(vehicle);
        
        // Update status to ASSIGNED
        OrderStatus assignedStatus = orderStatusRepository.findByStatusName(OrderStatusType.ASSIGNED.getStatusName())
                .orElseThrow(() -> new ResourceNotFoundException("ASSIGNED status not found"));
        order.setStatus(assignedStatus);
        
        return orderRepository.save(order);
    }

    /**
     * Get all vehicles in the system
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
    
    /**
     * Get all drivers in the system
     */
    public List<User> getAllDrivers() {
        return userRepository.findByRoleName(RoleType.Constants.DRIVER);
    }
    
    /**
     * Update order status
     */
    @Transactional
    public Order updateOrderStatus(Integer orderId, Integer statusId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (statusId == null) {
            throw new IllegalArgumentException("Status ID cannot be null");
        }
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        OrderStatus newStatus = orderStatusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Order status not found with id: " + statusId));
        
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
    
    /**
     * Create a new vehicle
     */
    @Transactional
    public Vehicle createVehicle(Vehicle vehicle) {
        // Verify that the driver exists and has DRIVER role
        User driver = userRepository.findById(vehicle.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        
        if (!RoleType.Constants.DRIVER.equals(driver.getRoleName())) {
            throw new IllegalArgumentException("Selected user is not a driver");
        }
        
        // Check if driver already has a vehicle
        if (vehicleRepository.findByDriver(driver).isPresent()) {
            throw new IllegalArgumentException("Driver already has a vehicle assigned");
        }
        
        return vehicleRepository.save(vehicle);
    }
    
    /**
     * Update an existing vehicle
     */
    @Transactional
    public Vehicle updateVehicle(Integer vehicleId, Vehicle updatedVehicle) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));
        
        // Update fields
        vehicle.setLicensePlate(updatedVehicle.getLicensePlate());
        vehicle.setCapacity(updatedVehicle.getCapacity());
        vehicle.setPhotoUrl(updatedVehicle.getPhotoUrl());
        
        // If driver is being changed, verify the new driver
        if (!vehicle.getDriver().getId().equals(updatedVehicle.getDriver().getId())) {
            User newDriver = userRepository.findById(updatedVehicle.getDriver().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
            
            if (!RoleType.Constants.DRIVER.equals(newDriver.getRoleName())) {
                throw new IllegalArgumentException("Selected user is not a driver");
            }
            
            // Check if new driver already has a vehicle
            if (vehicleRepository.findByDriver(newDriver).isPresent()) {
                throw new IllegalArgumentException("Driver already has a vehicle assigned");
            }
            
            vehicle.setDriver(newDriver);
        }
        
        return vehicleRepository.save(vehicle);
    }
    
    /**
     * Delete a vehicle
     */
    @Transactional
    public void deleteVehicle(Integer vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + vehicleId);
        }
        
        vehicleRepository.deleteById(vehicleId);
    }
    
    /**
     * Validate that the user has the SUPPORT_AGENT role
     */
    public void validateSupportAgentRole(Integer userId) {
        User user = userService.getUserById(userId);
        if (!RoleType.Constants.SUPPORT_AGENT.equals(user.getRoleName())) {
            throw new AccessDeniedException("This operation requires SUPPORT_AGENT role");
        }
    }
}
