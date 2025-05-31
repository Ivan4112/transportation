package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
