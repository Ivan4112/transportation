package org.edu.fpm.transportation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.edu.fpm.transportation.entity.Cargo;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CargoDto {
    private Integer id;
    private String type;
    private Double weight;
    
    public static CargoDto fromEntity(Cargo cargo) {
        if (cargo == null) {
            return null;
        }
        
        CargoDto dto = new CargoDto();
        dto.setId(cargo.getId());
        dto.setType(cargo.getType());
        dto.setWeight(cargo.getWeight());
        
        return dto;
    }
}
