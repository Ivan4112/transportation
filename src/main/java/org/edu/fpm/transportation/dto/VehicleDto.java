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
    private UserDto driver;
    private String licensePlate;
    private String photoUrl;
    private BigDecimal capacity;
    
    public static VehicleDto fromEntity(Vehicle vehicle) {
        VehicleDto dto = new VehicleDto();
        dto.setId(vehicle.getId());
        dto.setDriver(UserDto.fromEntity(vehicle.getDriver()));
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setPhotoUrl(vehicle.getPhotoUrl());
        dto.setCapacity(vehicle.getCapacity());
        return dto;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Integer id;
        private String firstName;
        private String lastName;
        
        public static UserDto fromEntity(User user) {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            return dto;
        }
    }
}
