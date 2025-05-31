package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.Cargo;
import org.edu.fpm.transportation.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Integer> {
    Optional<Cargo> findByOrder(Order order);
}
