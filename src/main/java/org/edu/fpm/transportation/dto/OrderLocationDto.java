package org.edu.fpm.transportation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.edu.fpm.transportation.entity.OrderLocation;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLocationDto {
    private Integer id;
    private Double latitude;
    private Double longitude;
    private Instant timestamp;
    private String statusComment;
    
    public static OrderLocationDto fromEntity(OrderLocation location) {
        if (location == null) {
            return null;
        }
        
        OrderLocationDto dto = new OrderLocationDto();
        dto.setId(location.getId());
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setTimestamp(location.getTimestamp());
        dto.setStatusComment(location.getStatusComment());
        
        return dto;
    }
}
