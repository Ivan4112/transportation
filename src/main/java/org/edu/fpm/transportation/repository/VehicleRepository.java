package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.User;
import org.edu.fpm.transportation.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByDriver(User driver);
}
