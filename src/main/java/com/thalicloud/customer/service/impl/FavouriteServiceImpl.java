package com.thalicloud.customer.service.impl;

import com.thalicloud.customer.dto.response.FavouriteResponse;
import com.thalicloud.customer.entity.Favourite;
import com.thalicloud.customer.exception.DuplicateResourceException;
import com.thalicloud.customer.exception.ResourceNotFoundException;
import com.thalicloud.customer.repository.FavouriteRepository;
import com.thalicloud.customer.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {

    private final FavouriteRepository favouriteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FavouriteResponse> getFavourites(String customerPhone) {
        log.info("getFavourites: start, customerPhone={}", customerPhone);
        try {
            List<FavouriteResponse> response = favouriteRepository
                    .findByCustomerPhoneOrderBySavedAtDesc(customerPhone)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            log.info("getFavourites: end, customerPhone={}", customerPhone);
            return response;
        } catch (Exception e) {
            log.error("getFavourites: failed, customerPhone={}", customerPhone, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public FavouriteResponse addFavourite(String customerPhone, UUID kitchenId) {
        log.info("addFavourite: start, customerPhone={}, kitchenId={}", customerPhone, kitchenId);
        try {
            if (favouriteRepository.existsByCustomerPhoneAndKitchenId(customerPhone, kitchenId)) {
                throw new DuplicateResourceException("Kitchen is already in favourites");
            }

            Favourite fav = Favourite.builder()
                    .customerPhone(customerPhone)
                    .kitchenId(kitchenId)
                    .build();

            log.debug("Adding kitchen {} to favourites for customer {}", kitchenId, customerPhone);
            FavouriteResponse response = toResponse(favouriteRepository.save(fav));
            log.info("addFavourite: end, customerPhone={}, kitchenId={}", customerPhone, kitchenId);
            return response;
        } catch (Exception e) {
            log.error("addFavourite: failed, customerPhone={}, kitchenId={}", customerPhone, kitchenId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void removeFavourite(String customerPhone, UUID kitchenId) {
        log.info("removeFavourite: start, customerPhone={}, kitchenId={}", customerPhone, kitchenId);
        try {
            if (!favouriteRepository.existsByCustomerPhoneAndKitchenId(customerPhone, kitchenId)) {
                throw new ResourceNotFoundException("Kitchen not found in favourites");
            }
            favouriteRepository.deleteByCustomerPhoneAndKitchenId(customerPhone, kitchenId);
            log.debug("Removed kitchen {} from favourites for customer {}", kitchenId, customerPhone);
            log.info("removeFavourite: end, customerPhone={}, kitchenId={}", customerPhone, kitchenId);
        } catch (Exception e) {
            log.error("removeFavourite: failed, customerPhone={}, kitchenId={}", customerPhone, kitchenId, e);
            throw e;
        }
    }

    private FavouriteResponse toResponse(Favourite f) {
        return FavouriteResponse.builder()
                .kitchenId(f.getKitchenId())
                .savedAt(f.getSavedAt())
                .build();
    }
}
