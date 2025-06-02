package org.edu.fpm.transportation.service;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.OrderStatusUpdateDto;
import org.edu.fpm.transportation.entity.*;
import org.edu.fpm.transportation.exception.AccessDeniedException;
import org.edu.fpm.transportation.exception.ResourceNotFoundException;
import org.edu.fpm.transportation.repository.OrderLocationRepository;
import org.edu.fpm.transportation.repository.OrderRepository;
import org.edu.fpm.transportation.repository.OrderStatusRepository;
import org.edu.fpm.transportation.repository.VehicleRepository;
import org.edu.fpm.transportation.util.OrderStatusType;
import org.edu.fpm.transportation.util.RoleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderLocationRepository orderLocationRepository;
    private final VehicleRepository vehicleRepository;
    private final UserService userService;

    /**
     * Get all orders assigned to the authenticated driver
     */
    public List<Order> getAssignedOrders(Integer userId) {
        User driver = userService.getUserById(userId);
        validateDriverRole(driver);
        return orderRepository.findByDriver(driver);
    }
    
    /**
     * Get a specific order by ID (only if assigned to the driver)
     */
    public Order getDriverOrderById(Integer orderId, Integer userId) {
        User driver = userService.getUserById(userId);
        validateDriverRole(driver);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        // Verify that this order is assigned to the authenticated driver
        if (order.getDriver() == null || !order.getDriver().getId().equals(driver.getId())) {
            throw new AccessDeniedException("You are not authorized to access this order");
        }
        
        return order;
    }

    /**
     * Update the status of an order
     * Only the assigned driver can update the status of their orders
     */
    @Transactional
    public Order updateOrderStatus(Integer orderId, OrderStatusUpdateDto statusUpdateDto,
                                   Integer userId) {
        User driver = userService.getUserById(userId);
        validateDriverRole(driver);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        // Verify that this order is assigned to the authenticated driver
        if (order.getDriver() == null || !order.getDriver().getId().equals(driver.getId())) {
            throw new AccessDeniedException("You are not authorized to update this order");
        }
        
        OrderStatus newStatus = orderStatusRepository.findById(statusUpdateDto.getStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Order status not found with id: " + statusUpdateDto.getStatusId()));
        
        // Verify that the driver can set this status
        validateDriverCanSetStatus(newStatus.getStatusName());
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        
        // If location data is provided, save it
        if (statusUpdateDto.getLatitude() != null && statusUpdateDto.getLongitude() != null) {
            OrderLocation location = new OrderLocation();
            location.setOrder(updatedOrder);
            location.setLatitude(statusUpdateDto.getLatitude());
            location.setLongitude(statusUpdateDto.getLongitude());
            location.setTimestamp(Instant.now());
            
            // If the DTO is actually a DriverOrderStatusDto with a comment
            if (statusUpdateDto instanceof org.edu.fpm.transportation.dto.DriverOrderStatusDto) {
                location.setStatusComment(((org.edu.fpm.transportation.dto.DriverOrderStatusDto) statusUpdateDto).getStatusComment());
            }
            
            orderLocationRepository.save(location);
        }
        
        return updatedOrder;
    }

    /**
     * Get information about the driver's vehicle
     */
    public Vehicle getDriverVehicle(Integer userId) {
        User driver = userService.getUserById(userId);
        validateDriverRole(driver);
        
        return vehicleRepository.findByDriver(driver)
                .orElseThrow(() -> new ResourceNotFoundException("No vehicle assigned to this driver"));
    }
    
    /**
     * Get all available order statuses that a driver can set
     */
    public List<OrderStatus> getDriverAvailableOrderStatuses() {
        // Get statuses that drivers are allowed to set
        List<String> driverAllowedStatusNames = Arrays.asList(
            OrderStatusType.IN_TRANSIT.getStatusName(),
            OrderStatusType.WAITING_UNLOADING.getStatusName(),
            OrderStatusType.DELIVERED.getStatusName()
        );
        
        return orderStatusRepository.findAll().stream()
            .filter(status -> driverAllowedStatusNames.contains(status.getStatusName()))
            .collect(Collectors.toList());
    }
    
    /**
     * Validate that the user has the DRIVER role
     */
    private void validateDriverRole(User user) {
        if (!RoleType.Constants.DRIVER.equals(user.getRoleName())) {
            throw new AccessDeniedException("This operation requires DRIVER role");
        }
    }
    
    /**
     * Validate that the driver can set this status
     * Drivers can only set specific statuses like "IN_TRANSIT", "WAITING_UNLOADING", "DELIVERED"
     */
    private void validateDriverCanSetStatus(String statusName) {
        List<String> driverAllowedStatusNames = Arrays.asList(
            OrderStatusType.IN_TRANSIT.getStatusName(),
            OrderStatusType.WAITING_UNLOADING.getStatusName(),
            OrderStatusType.DELIVERED.getStatusName()
        );
        
        if (!driverAllowedStatusNames.contains(statusName)) {
            throw new AccessDeniedException("Drivers are not authorized to set this order status: " + statusName);
        }
    }
}
