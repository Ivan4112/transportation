package org.edu.fpm.transportation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_status", schema = "transportation")
public class OrderStatus {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "status_name", nullable = false, length = 50)
    private String statusName;

}
