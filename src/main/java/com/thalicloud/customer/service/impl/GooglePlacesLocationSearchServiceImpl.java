package com.thalicloud.customer.service.impl;

import com.thalicloud.customer.dto.response.LocationSuggestion;
import com.thalicloud.customer.service.LocationSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Google Places Autocomplete implementation — not wired up as the active
 * {@link LocationSearchService} bean right now (see GeoapifyLocationSearchServiceImpl).
 * Kept around for a possible future switch back; not annotated with @Service on purpose.
 */
@Slf4j
@RequiredArgsConstructor
public class GooglePlacesLocationSearchServiceImpl implements LocationSearchService {

    private final RestTemplate restTemplate;

    @Value("${google.places.api-key}")
    private String apiKey;

    @Value("${google.places.autocomplete-url}")
    private String autocompleteUrl;

    @Override
    public List<LocationSuggestion> search(String query) {
        log.info("search: start, query={}", query);
        if (!StringUtils.hasText(apiKey)) {
            log.warn("GOOGLE_PLACES_API_KEY not configured — returning empty suggestions");
            log.info("search: end, query={}", query);
            return Collections.emptyList();
        }
        if (!StringUtils.hasText(query) || query.trim().length() < 2) {
            log.info("search: end, query={}", query);
            return Collections.emptyList();
        }

        String url = UriComponentsBuilder.fromHttpUrl(autocompleteUrl)
                .queryParam("input", query.trim())
                .queryParam("key", apiKey)
                .queryParam("components", "country:in")
                .queryParam("language", "en")
                .queryParam("types", "geocode")
                .toUriString();

        try {
            Map<String, Object> body = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            ).getBody();

            if (body == null || !"OK".equals(body.get("status"))) {
                log.warn("Google Places returned status: {}", body != null ? body.get("status") : "null");
                log.info("search: end, query={}", query);
                return Collections.emptyList();
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> predictions =
                    (List<Map<String, Object>>) body.getOrDefault("predictions", Collections.emptyList());

            List<LocationSuggestion> suggestions = predictions.stream()
                    .map(this::toPrediction)
                    .collect(Collectors.toList());
            log.info("search: end, query={}", query);
            return suggestions;

        } catch (RestClientException ex) {
            log.error("search: failed, query={}", query, ex);
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    private LocationSuggestion toPrediction(Map<String, Object> pred) {
        Map<String, Object> fmt = (Map<String, Object>)
                pred.getOrDefault("structured_formatting", Collections.emptyMap());

        return LocationSuggestion.builder()
                .placeId((String) pred.get("place_id"))
                .name((String) fmt.get("main_text"))
                .subtitle((String) fmt.get("secondary_text"))
                .build();
    }
}
