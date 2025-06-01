package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
    Optional<OrderStatus> findByStatusName(String statusName);
}
