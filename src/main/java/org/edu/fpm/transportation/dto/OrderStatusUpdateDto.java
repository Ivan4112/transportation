package org.edu.fpm.transportation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating order status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDto {
    private Integer statusId;
    private Double latitude;
    private Double longitude;
}
