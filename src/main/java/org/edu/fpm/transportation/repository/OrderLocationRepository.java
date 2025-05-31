package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.OrderLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderLocationRepository extends JpaRepository<OrderLocation, Integer> {
    List<OrderLocation> findByOrderOrderByTimestampDesc(Order order);
    Optional<OrderLocation> findFirstByOrderOrderByTimestampDesc(Order order);
}
