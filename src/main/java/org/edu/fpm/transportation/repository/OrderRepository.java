package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.Order;
import org.edu.fpm.transportation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomer(User customer);
    List<Order> findByDriver(User driver);
}
