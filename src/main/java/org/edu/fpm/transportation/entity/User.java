package org.edu.fpm.transportation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user", schema = "transportation")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "firstname", length = 100)
    private String firstName;
    
    @Column(name = "lastname", length = 100)
    private String lastName;
    
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;
}
