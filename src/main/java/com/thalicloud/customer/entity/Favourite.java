package com.thalicloud.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "favourites",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_favourite_customer_kitchen",
        columnNames = {"customer_phone", "kitchen_id"}
    ),
    indexes = {
        @Index(name = "idx_favourite_customer_phone", columnList = "customer_phone")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_phone", nullable = false, length = 15)
    private String customerPhone;

    @Column(name = "kitchen_id", nullable = false, updatable = false)
    private UUID kitchenId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onSave() {
        savedAt = LocalDateTime.now();
    }
}
