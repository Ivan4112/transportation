package org.edu.fpm.transportation.dto;

import lombok.Data;

@Data
public class OrderCreateDto {
    private String cargoType;
    private Double weight;
    private String startLocation;
    private String endLocation;
}
