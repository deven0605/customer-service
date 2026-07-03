package com.thalicloud.customer.service;

import com.thalicloud.customer.dto.request.SaveLocationRequest;
import com.thalicloud.customer.dto.response.CustomerLocationResponse;

public interface CustomerLocationService {

    CustomerLocationResponse saveLocation(String customerPhone, SaveLocationRequest request);

    CustomerLocationResponse getLocation(String customerPhone);
}
