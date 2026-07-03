package com.thalicloud.customer.repository;

import com.thalicloud.customer.entity.CustomerLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerLocationRepository extends JpaRepository<CustomerLocation, String> {
}
