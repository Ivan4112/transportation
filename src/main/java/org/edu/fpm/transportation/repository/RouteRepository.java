package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    Optional<Route> findByOrder(Order order);
}
