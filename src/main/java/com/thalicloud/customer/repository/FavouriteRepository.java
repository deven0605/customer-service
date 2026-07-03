package com.thalicloud.customer.repository;

import com.thalicloud.customer.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    List<Favourite> findByCustomerPhoneOrderBySavedAtDesc(String customerPhone);

    Optional<Favourite> findByCustomerPhoneAndKitchenId(String customerPhone, UUID kitchenId);

    boolean existsByCustomerPhoneAndKitchenId(String customerPhone, UUID kitchenId);

    void deleteByCustomerPhoneAndKitchenId(String customerPhone, UUID kitchenId);
}
