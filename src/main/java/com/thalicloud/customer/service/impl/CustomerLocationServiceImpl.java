package com.thalicloud.customer.service.impl;

import com.thalicloud.customer.dto.request.SaveLocationRequest;
import com.thalicloud.customer.dto.response.CustomerLocationResponse;
import com.thalicloud.customer.entity.CustomerLocation;
import com.thalicloud.customer.exception.ResourceNotFoundException;
import com.thalicloud.customer.repository.CustomerLocationRepository;
import com.thalicloud.customer.service.CustomerLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerLocationServiceImpl implements CustomerLocationService {

    private final CustomerLocationRepository locationRepository;

    @Override
    @Transactional
    public CustomerLocationResponse saveLocation(String customerPhone, SaveLocationRequest req) {
        // Build entity with customerPhone as PK — Spring Data JPA calls em.merge(), which
        // inserts on first save and updates on subsequent calls (natural upsert behaviour).
        CustomerLocation location = CustomerLocation.builder()
                .customerPhone(customerPhone)
                .lat(req.getLat())
                .lng(req.getLng())
                .address(req.getAddress())
                .build();

        CustomerLocation saved = locationRepository.save(location);
        log.debug("Saved location for customer {}: lat={}, lng={}", customerPhone, req.getLat(), req.getLng());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerLocationResponse getLocation(String customerPhone) {
        CustomerLocation location = locationRepository.findById(customerPhone)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No location saved for this customer yet"));
        return toResponse(location);
    }

    private CustomerLocationResponse toResponse(CustomerLocation loc) {
        return CustomerLocationResponse.builder()
                .lat(loc.getLat())
                .lng(loc.getLng())
                .address(loc.getAddress())
                .updatedAt(loc.getUpdatedAt())
                .build();
    }
}
