package org.edu.fpm.transportation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for driver to update order status with optional location and comments
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverOrderStatusDto extends OrderStatusUpdateDto {
    private String statusComment;
    
    public DriverOrderStatusDto(Integer statusId, Double latitude, Double longitude, String statusComment) {
        super(statusId, latitude, longitude);
        this.statusComment = statusComment;
    }
}
