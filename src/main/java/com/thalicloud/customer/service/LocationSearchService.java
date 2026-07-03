package com.thalicloud.customer.service;

import com.thalicloud.customer.dto.response.LocationSuggestion;

import java.util.List;

public interface LocationSearchService {

    List<LocationSuggestion> search(String query);
}
