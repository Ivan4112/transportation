package org.edu.fpm.transportation.service;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.notification.NotificationDto;
import org.edu.fpm.transportation.entity.Notification;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.repository.NotificationRepository;
import org.edu.fpm.transportation.repository.OrderRepository;
import org.edu.fpm.transportation.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    /**
     * Create a notification for order status change
     */
    public void createOrderStatusNotification(Integer orderId, String statusName) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        
        User customer = order.getCustomer();
        
        String message = String.format("Your order #%d status has been updated to: %s", orderId, statusName);
        
        Notification notification = new Notification();
        notification.setUser(customer);
        notification.setOrder(order);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(Instant.now());
        
        notificationRepository.save(notification);
    }
    
    /**
     * Get all notifications for a user
     */
    public List<NotificationDto> getUserNotifications(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get unread notifications for a user
     */
    public List<NotificationDto> getUnreadNotifications(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Mark a notification as read
     */
    public NotificationDto markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Notification not found with id: " + notificationId));
        
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        
        return convertToDto(notification);
    }
    
    /**
     * Mark all notifications for a user as read
     */
    public void markAllAsRead(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        
        for (Notification notification : notifications) {
            notification.setIsRead(true);
        }
        
        notificationRepository.saveAll(notifications);
    }
    
    /**
     * Get count of unread notifications for a user
     */
    public long getUnreadCount(Integer userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    /**
     * Convert entity to DTO
     */
    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setOrderId(notification.getOrder().getId());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
