package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Integer userId);
    List<Notification> findByOrderIdAndUserId(Integer orderId, Integer userId);
    long countByUserIdAndIsReadFalse(Integer userId);
}
