package org.edu.fpm.transportation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.entity.Vehicle;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    private Integer id;
    private String licensePlate;
    private BigDecimal capacity;
    private String photoUrl;
    private UserDto driver;
    
    public static VehicleDto fromEntity(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        
        VehicleDto dto = new VehicleDto();
        dto.setId(vehicle.getId());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setCapacity(vehicle.getCapacity());
        dto.setPhotoUrl(vehicle.getPhotoUrl());
        
        // Include driver information if available
        if (vehicle.getDriver() != null) {
            dto.setDriver(UserDto.fromEntity(vehicle.getDriver()));
        }
        
        return dto;
    }
}
