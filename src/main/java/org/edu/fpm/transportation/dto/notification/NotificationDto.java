package org.edu.fpm.transportation.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Integer id;
    private Integer orderId;
    private String message;
    private Boolean isRead;
    private Instant createdAt;
}
