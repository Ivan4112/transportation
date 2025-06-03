package org.edu.fpm.transportation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.edu.fpm.transportation.entity.Route;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {
    private Integer id;
    private String startLocation;
    private String endLocation;
    private BigDecimal distance;
    private Instant estimatedTime;
    
    public static RouteDto fromEntity(Route route) {
        if (route == null) {
            return null;
        }
        
        RouteDto dto = new RouteDto();
        dto.setId(route.getId());
        dto.setStartLocation(route.getStartLocation());
        dto.setEndLocation(route.getEndLocation());
        dto.setDistance(route.getDistance());
        dto.setEstimatedTime(route.getEstimatedTime());
        
        return dto;
    }
}
