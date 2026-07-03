package com.thalicloud.customer.controller;

import com.thalicloud.customer.dto.request.SaveLocationRequest;
import com.thalicloud.customer.dto.response.ApiResponse;
import com.thalicloud.customer.dto.response.CustomerLocationResponse;
import com.thalicloud.customer.dto.response.LocationSuggestion;
import com.thalicloud.customer.security.CustomerPrincipal;
import com.thalicloud.customer.service.CustomerLocationService;
import com.thalicloud.customer.service.LocationSearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * All location endpoints live under /api/customer/location so the gateway
 * needs exactly one route predicate: Path=/api/customer/**
 */
@Validated
@RestController
@RequestMapping("/api/customer/location")
@RequiredArgsConstructor
public class CustomerLocationController {

    private final CustomerLocationService locationService;
    private final LocationSearchService locationSearchService;

    // S06 — customer confirms their delivery zone after GPS / map pin
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerLocationResponse>> saveLocation(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @Valid @RequestBody SaveLocationRequest request) {

        CustomerLocationResponse response = locationService.saveLocation(principal.getPhone(), request);
        return ResponseEntity.ok(ApiResponse.success("Location saved successfully", response));
    }

    // Retrieve the stored delivery zone (used when reopening the app)
    @GetMapping
    public ResponseEntity<ApiResponse<CustomerLocationResponse>> getLocation(
            @AuthenticationPrincipal CustomerPrincipal principal) {

        CustomerLocationResponse response = locationService.getLocation(principal.getPhone());
        return ResponseEntity.ok(ApiResponse.success("Location retrieved", response));
    }

    // S07 — proxy to Geoapify Autocomplete; keeps the API key server-side
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LocationSuggestion>>> search(
            @RequestParam @NotBlank @Size(min = 2, max = 200) String q) {

        List<LocationSuggestion> suggestions = locationSearchService.search(q);
        return ResponseEntity.ok(ApiResponse.success("Suggestions fetched", suggestions));
    }
}


