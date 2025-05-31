package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {
}
