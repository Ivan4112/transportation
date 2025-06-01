package org.edu.fpm.transportation.controller;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.notification.NotificationDto;
import org.edu.fpm.transportation.dto.order.OrderRequestDto;
import org.edu.fpm.transportation.dto.order.OrderResponseDto;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.service.CustomerOrderService;
import org.edu.fpm.transportation.service.NotificationService;
import org.edu.fpm.transportation.service.UserService;
import org.edu.fpm.transportation.util.RoleType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;

/**
 * Controller for web pages using Thymeleaf templates
 */
@Controller
@RequiredArgsConstructor
public class WebController {

    private final CustomerOrderService customerOrderService;
    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * Home page
     */
    @GetMapping("/")
    public String home(Model model) {
        addUnreadNotificationsCount(model);
        return "index";
    }

    /**
     * Create order page for customers
     */
    @GetMapping("/customer/orders/create")
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public String createOrder(Model model) {
        model.addAttribute("orderRequest", new OrderRequestDto());
        addUnreadNotificationsCount(model);
        return "customer/create-order";
    }

    /**
     * Customer orders list page
     */
    @GetMapping("/customer/orders")
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public String customerOrders(Model model) {
        Integer customerId = getCurrentUserId();
        List<OrderResponseDto> orders = Collections.emptyList();
        
        if (customerId != null) {
            orders = customerOrderService.getCustomerOrders(customerId);
        }
        
        model.addAttribute("orders", orders);
        addUnreadNotificationsCount(model);
        
        return "customer/orders";
    }

    /**
     * Customer order details page
     */
    @GetMapping("/customer/orders/{orderId}")
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public String orderDetails(@PathVariable Integer orderId, Model model) {
        Integer customerId = getCurrentUserId();
        
        if (customerId != null) {
            OrderResponseDto order = customerOrderService.getCustomerOrder(customerId, orderId);
            model.addAttribute("order", order);
        }
        
        addUnreadNotificationsCount(model);
        return "customer/order-details";
    }

    /**
     * Notifications page
     */
    @GetMapping("/notifications")
    @PreAuthorize("isAuthenticated()")
    public String notifications(Model model) {
        Integer userId = getCurrentUserId();
        List<NotificationDto> notifications = Collections.emptyList();
        
        if (userId != null) {
            notifications = notificationService.getUserNotifications(userId);
        }
        
        model.addAttribute("notifications", notifications);
        addUnreadNotificationsCount(model);
        
        return "notifications";
    }

    /**
     * Helper method to add unread notifications count to model
     */
    private void addUnreadNotificationsCount(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                Integer userId = getCurrentUserId();
                if (userId != null) {
                    long unreadCount = notificationService.getUnreadCount(userId);
                    model.addAttribute("unreadNotifications", unreadCount);
                    return;
                }
            }
        } catch (Exception e) {
            // If there's any error, just set count to 0
        }
        
        model.addAttribute("unreadNotifications", 0);
    }

    /**
     * Helper method to get current user ID
     */
    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            return userService.getUserByEmail(email)
                    .map(User::getId)
                    .orElse(null);
        }
        return null;
    }
}
