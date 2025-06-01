package org.edu.fpm.transportation.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Integer id;
    private String customerName;
    private String driverName;
    private String vehicleLicensePlate;
    private String status;
    private BigDecimal price;
    private String cargoType;
    private Double cargoWeight;
    private String startLocation;
    private String endLocation;
    private BigDecimal distance;
    private Instant createdAt;
    private Instant estimatedDeliveryTime;
}
