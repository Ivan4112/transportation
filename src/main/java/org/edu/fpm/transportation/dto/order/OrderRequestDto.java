package org.edu.fpm.transportation.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private String cargoType;
    private Double cargoWeight; // Weight in kilograms
    private String startLocation;
    private String endLocation;
}
