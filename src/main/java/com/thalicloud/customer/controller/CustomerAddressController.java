package com.thalicloud.customer.controller;

import com.thalicloud.customer.dto.request.AddressRequest;
import com.thalicloud.customer.dto.response.AddressResponse;
import com.thalicloud.customer.dto.response.ApiResponse;
import com.thalicloud.customer.security.CustomerPrincipal;
import com.thalicloud.customer.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer/addresses")
@RequiredArgsConstructor
public class CustomerAddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal CustomerPrincipal principal) {

        List<AddressResponse> addresses = addressService.getAddresses(principal.getPhone());
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved", addresses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @Valid @RequestBody AddressRequest request) {

        AddressResponse address = addressService.addAddress(principal.getPhone(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added", address));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest request) {

        AddressResponse address = addressService.updateAddress(principal.getPhone(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated", address));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID id) {

        addressService.deleteAddress(principal.getPhone(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted"));
    }

    @PostMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID id) {

        AddressResponse address = addressService.setDefaultAddress(principal.getPhone(), id);
        return ResponseEntity.ok(ApiResponse.success("Default address updated", address));
    }
}
