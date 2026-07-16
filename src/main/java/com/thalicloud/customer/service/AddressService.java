package com.thalicloud.customer.service;

import com.thalicloud.customer.dto.request.AddressRequest;
import com.thalicloud.customer.dto.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    List<AddressResponse> getAddresses(String customerPhone);

    AddressResponse addAddress(String customerPhone, AddressRequest request);

    AddressResponse updateAddress(String customerPhone, UUID addressId, AddressRequest request);

    void deleteAddress(String customerPhone, UUID addressId);

    AddressResponse setDefaultAddress(String customerPhone, UUID addressId);
}
