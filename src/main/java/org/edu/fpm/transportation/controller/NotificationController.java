package org.edu.fpm.transportation.controller;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.notification.NotificationDto;
import org.edu.fpm.transportation.service.NotificationService;
import org.edu.fpm.transportation.service.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService jwtService;

    /**
     * Get all notifications for the authenticated user
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<NotificationDto> getUserNotifications(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userId = Integer.valueOf(jwtService.getUserIdFromToken(token));
        
        return notificationService.getUserNotifications(userId);
    }

    /**
     * Get unread notifications for the authenticated user
     */
    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public List<NotificationDto> getUnreadNotifications(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        Integer userId = Integer.valueOf(jwtService.getUserIdFromToken(token));
        
        return notificationService.getUnreadNotifications(userId);
    }

    /**
     * Get count of unread notifications
     */
    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userId = Integer.valueOf(jwtService.getUserIdFromToken(token));
        
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Mark a notification as read
     */
    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public NotificationDto markAsRead(@PathVariable Integer notificationId) {
        return notificationService.markAsRead(notificationId);
    }

    /**
     * Mark all notifications as read
     */
    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAllAsRead(@RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromHeaders(Map.of(HttpHeaders.AUTHORIZATION, authHeader));
        Integer userId = Integer.valueOf(jwtService.getUserIdFromToken(token));
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
