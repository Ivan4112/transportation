package org.edu.fpm.transportation.controller;

import lombok.RequiredArgsConstructor;
import org.edu.fpm.transportation.dto.auth.signup.SignUpRequestDto;
import org.edu.fpm.transportation.dto.order.OrderRequestDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.edu.fpm.transportation.util.RoleType;

/**
 * Controller for rendering view templates
 * This controller only returns views and doesn't contain business logic
 */
@Controller
@RequiredArgsConstructor
public class ViewController {

    /**
     * Home page
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Registration page
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("signUpRequest", new SignUpRequestDto());
        return "register";
    }

    /**
     * About Us page
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    /**
     * Services page
     */
    @GetMapping("/services")
    public String services() {
        return "services";
    }
    
    /**
     * Contact page
     */
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    /**
     * Create order page for customers
     */
    @GetMapping("/customer/orders/create")
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public String createOrder(Model model) {
        model.addAttribute("orderRequest", new OrderRequestDto());
        return "customer/create-order";
    }

    /**
     * Customer orders list page
     */
    @GetMapping("/customer/orders")
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public String customerOrders() {
        return "customer/orders";
    }

    /**
     * Customer order details page
     */
    @GetMapping("/customer/orders/{orderId}")
    @PreAuthorize("hasRole('" + RoleType.Constants.CUSTOMER + "')")
    public String orderDetails(@PathVariable Integer orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "customer/order-details";
    }

    /**
     * Notifications page
     */
    @GetMapping("/notifications")
    @PreAuthorize("isAuthenticated()")
    public String notifications() {
        return "notifications";
    }
    
    /**
     * Driver orders list page
     */
    @GetMapping("/driver/orders")
    @PreAuthorize("hasRole('" + RoleType.Constants.DRIVER + "')")
    public String driverOrders() {
        return "driver/orders";
    }
    
    /**
     * Driver order details page
     */
    @GetMapping("/driver/orders/{orderId}")
    @PreAuthorize("hasRole('" + RoleType.Constants.DRIVER + "')")
    public String driverOrderDetails(@PathVariable Integer orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "driver/order-details";
    }
    
    /**
     * Support agent dashboard
     */
    @GetMapping("/support/dashboard")
    @PreAuthorize("hasRole('" + RoleType.Constants.SUPPORT_AGENT + "')")
    public String supportDashboard() {
        return "support/dashboard";
    }

    /**
     * Support agent order management
     */
    @GetMapping("/support/orders")
    @PreAuthorize("hasRole('" + RoleType.Constants.SUPPORT_AGENT + "')")
    public String supportOrders() {
        return "support/orders";
    }

    /**
     * Support agent order details
     */
    @GetMapping("/support/orders/{orderId}")
    @PreAuthorize("hasRole('" + RoleType.Constants.SUPPORT_AGENT + "')")
    public String supportOrderDetails(@PathVariable Integer orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "support/order-details";
    }

    /**
     * Support agent vehicle management
     */
    @GetMapping("/support/vehicles")
    @PreAuthorize("hasRole('" + RoleType.Constants.SUPPORT_AGENT + "')")
    public String supportVehicles() {
        return "support/vehicles";
    }

    /**
     * Support agent vehicle form (create/edit)
     */
    @GetMapping("/support/vehicles/form")
    @PreAuthorize("hasRole('" + RoleType.Constants.SUPPORT_AGENT + "')")
    public String supportVehicleForm() {
        return "support/vehicle-form";
    }

    /**
     * Support agent vehicle edit form
     */
    @GetMapping("/support/vehicles/{vehicleId}/edit")
    @PreAuthorize("hasRole('" + RoleType.Constants.SUPPORT_AGENT + "')")
    public String supportVehicleEdit(@PathVariable Integer vehicleId, Model model) {
        model.addAttribute("vehicleId", vehicleId);
        return "support/vehicle-form";
    }
    
    /**
     * Admin dashboard
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('" + RoleType.Constants.ADMIN + "')")
    public String adminDashboard() {
        return "admin/dashboard";
    }
    
    /**
     * Admin user role management
     */
    @GetMapping("/admin/users/roles")
    @PreAuthorize("hasRole('" + RoleType.Constants.ADMIN + "')")
    public String adminUserRoles() {
        return "admin/user-roles";
    }
}
