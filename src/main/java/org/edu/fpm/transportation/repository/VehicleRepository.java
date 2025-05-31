package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
}
