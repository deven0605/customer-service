package com.thalicloud.customer.entity;

import com.thalicloud.customer.enums.AddressLabel;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "addresses", indexes = {
        @Index(name = "idx_address_customer_phone", columnList = "customer_phone")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_phone", nullable = false, length = 15)
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AddressLabel label;

    @Column(nullable = false, length = 50)
    private String flatNo;

    @Column(length = 100)
    private String building;

    @Column(nullable = false, length = 150)
    private String street;

    @Column(nullable = false, length = 100)
    private String area;

    @Column(length = 150)
    private String landmark;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 10)
    private String pinCode;

    private Double lat;
    private Double lng;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private boolean defaultAddress = false;
}
