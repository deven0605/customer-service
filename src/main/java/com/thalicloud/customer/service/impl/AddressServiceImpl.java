package com.thalicloud.customer.service.impl;

import com.thalicloud.customer.dto.request.AddressRequest;
import com.thalicloud.customer.dto.response.AddressResponse;
import com.thalicloud.customer.entity.Address;
import com.thalicloud.customer.exception.ResourceNotFoundException;
import com.thalicloud.customer.repository.AddressRepository;
import com.thalicloud.customer.service.AddressService;
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
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(String customerPhone) {
        log.info("getAddresses: start, customerPhone={}", customerPhone);
        try {
            List<AddressResponse> response = addressRepository
                    .findByCustomerPhoneOrderByDefaultAddressDescIdAsc(customerPhone)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            log.info("getAddresses: end, customerPhone={}", customerPhone);
            return response;
        } catch (Exception e) {
            log.error("getAddresses: failed, customerPhone={}", customerPhone, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public AddressResponse addAddress(String customerPhone, AddressRequest req) {
        log.info("addAddress: start, customerPhone={}", customerPhone);
        try {
            // The customer's very first address is always the default, regardless of what
            // the client sent — there must never be zero default addresses once one exists.
            boolean isFirstAddress = addressRepository
                    .findByCustomerPhoneOrderByDefaultAddressDescIdAsc(customerPhone).isEmpty();
            boolean shouldBeDefault = req.isDefaultAddress() || isFirstAddress;

            if (shouldBeDefault) {
                clearExistingDefault(customerPhone);
            }

            Address address = Address.builder()
                    .customerPhone(customerPhone)
                    .label(req.getLabel())
                    .flatNo(req.getFlatNo())
                    .building(req.getBuilding())
                    .street(req.getStreet())
                    .area(req.getArea())
                    .landmark(req.getLandmark())
                    .city(req.getCity())
                    .pinCode(req.getPinCode())
                    .lat(req.getLat())
                    .lng(req.getLng())
                    .defaultAddress(shouldBeDefault)
                    .build();

            log.debug("Adding address for customer {}", customerPhone);
            AddressResponse response = toResponse(addressRepository.save(address));
            log.info("addAddress: end, customerPhone={}", customerPhone);
            return response;
        } catch (Exception e) {
            log.error("addAddress: failed, customerPhone={}", customerPhone, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(String customerPhone, UUID addressId, AddressRequest req) {
        log.info("updateAddress: start, customerPhone={}, addressId={}", customerPhone, addressId);
        try {
            Address address = addressRepository.findByIdAndCustomerPhone(addressId, customerPhone)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Address not found: " + addressId));

            if (req.isDefaultAddress() && !address.isDefaultAddress()) {
                clearExistingDefault(customerPhone);
            }

            address.setLabel(req.getLabel());
            address.setFlatNo(req.getFlatNo());
            address.setBuilding(req.getBuilding());
            address.setStreet(req.getStreet());
            address.setArea(req.getArea());
            address.setLandmark(req.getLandmark());
            address.setCity(req.getCity());
            address.setPinCode(req.getPinCode());
            address.setLat(req.getLat());
            address.setLng(req.getLng());
            // Never leave the customer with zero default addresses: ignore an explicit
            // "unset" if this was the only default they had.
            address.setDefaultAddress(req.isDefaultAddress() || address.isDefaultAddress());

            AddressResponse response = toResponse(addressRepository.save(address));
            log.info("updateAddress: end, customerPhone={}, addressId={}", customerPhone, addressId);
            return response;
        } catch (Exception e) {
            log.error("updateAddress: failed, customerPhone={}, addressId={}", customerPhone, addressId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteAddress(String customerPhone, UUID addressId) {
        log.info("deleteAddress: start, customerPhone={}, addressId={}", customerPhone, addressId);
        try {
            Address address = addressRepository.findByIdAndCustomerPhone(addressId, customerPhone)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Address not found: " + addressId));

            boolean wasDefault = address.isDefaultAddress();
            addressRepository.delete(address);
            log.debug("Deleted address {} for customer {}", addressId, customerPhone);

            if (wasDefault) {
                addressRepository.findByCustomerPhoneOrderByDefaultAddressDescIdAsc(customerPhone)
                        .stream()
                        .findFirst()
                        .ifPresent(next -> {
                            next.setDefaultAddress(true);
                            addressRepository.save(next);
                        });
            }
            log.info("deleteAddress: end, customerPhone={}, addressId={}", customerPhone, addressId);
        } catch (Exception e) {
            log.error("deleteAddress: failed, customerPhone={}, addressId={}", customerPhone, addressId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(String customerPhone, UUID addressId) {
        log.info("setDefaultAddress: start, customerPhone={}, addressId={}", customerPhone, addressId);
        try {
            Address address = addressRepository.findByIdAndCustomerPhone(addressId, customerPhone)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Address not found: " + addressId));

            if (!address.isDefaultAddress()) {
                clearExistingDefault(customerPhone);
                address.setDefaultAddress(true);
                addressRepository.save(address);
            }

            AddressResponse response = toResponse(address);
            log.info("setDefaultAddress: end, customerPhone={}, addressId={}", customerPhone, addressId);
            return response;
        } catch (Exception e) {
            log.error("setDefaultAddress: failed, customerPhone={}, addressId={}", customerPhone, addressId, e);
            throw e;
        }
    }

    private void clearExistingDefault(String customerPhone) {
        List<Address> currentDefaults = addressRepository.findByCustomerPhoneAndDefaultAddressTrue(customerPhone);
        if (currentDefaults.isEmpty()) {
            return;
        }
        currentDefaults.forEach(a -> a.setDefaultAddress(false));
        addressRepository.saveAll(currentDefaults);
    }

    private AddressResponse toResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .label(a.getLabel())
                .flatNo(a.getFlatNo())
                .building(a.getBuilding())
                .street(a.getStreet())
                .area(a.getArea())
                .landmark(a.getLandmark())
                .city(a.getCity())
                .pinCode(a.getPinCode())
                .lat(a.getLat())
                .lng(a.getLng())
                .defaultAddress(a.isDefaultAddress())
                .build();
    }
}
