package com.thalicloud.customer.repository;

import com.thalicloud.customer.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByCustomerPhoneOrderByDefaultAddressDescIdAsc(String customerPhone);

    List<Address> findByCustomerPhoneAndDefaultAddressTrue(String customerPhone);

    Optional<Address> findByIdAndCustomerPhone(UUID id, String customerPhone);

    boolean existsByIdAndCustomerPhone(UUID id, String customerPhone);
}
