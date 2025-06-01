package org.edu.fpm.transportation.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPriceResponseDto {
    private BigDecimal price;
    private BigDecimal distance;
    private String cargoType;
    private Double cargoWeight;
    private String startLocation;
    private String endLocation;
}
