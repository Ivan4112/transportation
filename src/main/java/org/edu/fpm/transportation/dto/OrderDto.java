package org.edu.fpm.transportation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.OrderStatus;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.entity.Vehicle;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Integer id;
    private UserDto customer;
    private UserDto driver;
    private VehicleDto vehicle;
    private OrderStatusDto status;
    private BigDecimal price;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
    private Instant createdAt;
    
    public static OrderDto fromEntity(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCustomer(UserDto.fromEntity(order.getCustomer()));
        
        if (order.getDriver() != null) {
            dto.setDriver(UserDto.fromEntity(order.getDriver()));
        }
        
        if (order.getVehicle() != null) {
            dto.setVehicle(VehicleDto.fromEntity(order.getVehicle()));
        }
        
        dto.setStatus(OrderStatusDto.fromEntity(order.getStatus()));
        dto.setPrice(order.getPrice());
        dto.setCreatedAt(order.getCreatedAt());
        
        return dto;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Integer id;
        private String email;
        private String firstName;
        private String lastName;
        private String roleName;
        
        public static UserDto fromEntity(User user) {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setRoleName(user.getRoleName());
            return dto;
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleDto {
        private Integer id;
        private String licensePlate;
        private BigDecimal capacity;
        private String photoUrl;
        
        public static VehicleDto fromEntity(Vehicle vehicle) {
            VehicleDto dto = new VehicleDto();
            dto.setId(vehicle.getId());
            dto.setLicensePlate(vehicle.getLicensePlate());
            dto.setCapacity(vehicle.getCapacity());
            dto.setPhotoUrl(vehicle.getPhotoUrl());
            return dto;
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatusDto {
        private Integer id;
        private String statusName;
        
        public static OrderStatusDto fromEntity(OrderStatus status) {
            OrderStatusDto dto = new OrderStatusDto();
            dto.setId(status.getId());
            dto.setStatusName(status.getStatusName());
            return dto;
        }
    }
}
