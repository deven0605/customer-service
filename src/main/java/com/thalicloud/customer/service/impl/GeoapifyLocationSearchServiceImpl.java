package com.thalicloud.customer.service.impl;

import com.thalicloud.customer.dto.response.LocationSuggestion;
import com.thalicloud.customer.service.LocationSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Geoapify Autocomplete implementation — currently the active
 * {@link LocationSearchService} bean. See GooglePlacesLocationSearchServiceImpl
 * for the previous provider, kept for a possible future switch back.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeoapifyLocationSearchServiceImpl implements LocationSearchService {

    private final RestTemplate restTemplate;

    @Value("${geoapify.api-key}")
    private String apiKey;

    @Value("${geoapify.autocomplete-url}")
    private String autocompleteUrl;

    @Override
    public List<LocationSuggestion> search(String query) {
        if (!StringUtils.hasText(apiKey)) {
            log.warn("GEOAPIFY_API_KEY not configured — returning empty suggestions");
            return Collections.emptyList();
        }
        if (!StringUtils.hasText(query) || query.trim().length() < 2) {
            return Collections.emptyList();
        }

        String url = UriComponentsBuilder.fromHttpUrl(autocompleteUrl)
                .queryParam("text", query.trim())
                .queryParam("apiKey", apiKey)
                .queryParam("filter", "countrycode:in")
                .queryParam("lang", "en")
                .queryParam("format", "json")
                .toUriString();

System.out.println("Geoapify Autocomplete URL: " + url); // Debugging line
        try {
            Map<String, Object> body = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            ).getBody();
System.out.println("body: " + body); // Debugging line
            if (body == null) {
                log.warn("Geoapify returned an empty response body");
                return Collections.emptyList();
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results =
                    (List<Map<String, Object>>) body.getOrDefault("results", Collections.emptyList());

            return results.stream()
                    .map(this::toSuggestion)
                    .collect(Collectors.toList());

        } catch (RestClientException ex) {
            log.error("Geoapify API call failed: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    private LocationSuggestion toSuggestion(Map<String, Object> result) {
        return LocationSuggestion.builder()
                .placeId((String) result.get("place_id"))
                .name((String) result.get("address_line1"))
                .subtitle((String) result.get("address_line2"))
                .latitude(toDouble(result.get("lat")))
                .longitude(toDouble(result.get("lon")))
                .build();
    }

    private Double toDouble(Object value) {
        return value instanceof Number ? ((Number) value).doubleValue() : null;
    }
}
