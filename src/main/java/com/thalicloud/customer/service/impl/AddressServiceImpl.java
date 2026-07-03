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
        return addressRepository
                .findByCustomerPhoneOrderByDefaultAddressDescIdAsc(customerPhone)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse addAddress(String customerPhone, AddressRequest req) {
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
                .defaultAddress(req.isDefaultAddress())
                .build();

        log.debug("Adding address for customer {}", customerPhone);
        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(String customerPhone, UUID addressId, AddressRequest req) {
        Address address = addressRepository.findByIdAndCustomerPhone(addressId, customerPhone)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found: " + addressId));

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
        address.setDefaultAddress(req.isDefaultAddress());

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(String customerPhone, UUID addressId) {
        Address address = addressRepository.findByIdAndCustomerPhone(addressId, customerPhone)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found: " + addressId));
        addressRepository.delete(address);
        log.debug("Deleted address {} for customer {}", addressId, customerPhone);
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
