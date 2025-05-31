package org.edu.fpm.transportation.dto;

import lombok.Data;

@Data
public class OrderStatusUpdateDto {
    private Integer statusId;
    private Double latitude;
    private Double longitude;
}
