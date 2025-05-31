package org.edu.fpm.transportation.repository;

import org.edu.fpm.transportation.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(String roleName);
    Optional<Role> findByEmail(String email);
}
