package com.thalicloud.customer.controller;

import com.thalicloud.customer.dto.request.SaveLocationRequest;
import com.thalicloud.customer.dto.response.ApiResponse;
import com.thalicloud.customer.dto.response.CustomerLocationResponse;
import com.thalicloud.customer.dto.response.LocationSuggestion;
import com.thalicloud.customer.security.CustomerPrincipal;
import com.thalicloud.customer.service.CustomerLocationService;
import com.thalicloud.customer.service.LocationSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * All location endpoints live under /api/customer/location so the gateway
 * needs exactly one route predicate: Path=/api/customer/**
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/customer/location")
@RequiredArgsConstructor
@Tag(name = "Customer Location", description = "Save, retrieve, and search the authenticated customer's live delivery location/zone.")
public class CustomerLocationController {

    private final CustomerLocationService locationService;
    private final LocationSearchService locationSearchService;

    // S06 — customer confirms their delivery zone after GPS / map pin
    @Operation(summary = "Save/confirm delivery location",
            description = "Persists the customer's confirmed delivery location (lat, lng and a human-readable address) after they pick it via GPS or a map pin. Overwrites any previously saved location for the customer.")
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerLocationResponse>> saveLocation(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @Valid @RequestBody SaveLocationRequest request) {
        log.info("saveLocation: start, phone={}", principal.getPhone());
        try {
            CustomerLocationResponse response = locationService.saveLocation(principal.getPhone(), request);
            ResponseEntity<ApiResponse<CustomerLocationResponse>> result =
                    ResponseEntity.ok(ApiResponse.success("Location saved successfully", response));
            log.info("saveLocation: end, phone={}", principal.getPhone());
            return result;
        } catch (Exception e) {
            log.error("saveLocation: failed, phone={}", principal.getPhone(), e);
            throw e;
        }
    }

    // Retrieve the stored delivery zone (used when reopening the app)
    @Operation(summary = "Get saved delivery location",
            description = "Returns the authenticated customer's most recently saved delivery location, used to restore the delivery zone when the app is reopened.")
    @GetMapping
    public ResponseEntity<ApiResponse<CustomerLocationResponse>> getLocation(
            @AuthenticationPrincipal CustomerPrincipal principal) {
        log.info("getLocation: start, phone={}", principal.getPhone());
        try {
            CustomerLocationResponse response = locationService.getLocation(principal.getPhone());
            ResponseEntity<ApiResponse<CustomerLocationResponse>> result =
                    ResponseEntity.ok(ApiResponse.success("Location retrieved", response));
            log.info("getLocation: end, phone={}", principal.getPhone());
            return result;
        } catch (Exception e) {
            log.error("getLocation: failed, phone={}", principal.getPhone(), e);
            throw e;
        }
    }

    // S07 — proxy to Geoapify Autocomplete; keeps the API key server-side
    @Operation(summary = "Search location suggestions",
            description = "Proxies an address autocomplete query (2-200 chars) to the configured location-search provider (e.g. Geoapify) and returns matching suggestions, keeping the provider's API key server-side.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LocationSuggestion>>> search(
            @RequestParam @NotBlank @Size(min = 2, max = 200) String q) {
        log.info("search: start, q={}", q);
        try {
            List<LocationSuggestion> suggestions = locationSearchService.search(q);
            ResponseEntity<ApiResponse<List<LocationSuggestion>>> response =
                    ResponseEntity.ok(ApiResponse.success("Suggestions fetched", suggestions));
            log.info("search: end, q={}", q);
            return response;
        } catch (Exception e) {
            log.error("search: failed, q={}", q, e);
            throw e;
        }
    }
}
