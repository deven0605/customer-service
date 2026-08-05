package com.thalicloud.customer.controller;

import com.thalicloud.customer.dto.request.AddressRequest;
import com.thalicloud.customer.dto.response.AddressResponse;
import com.thalicloud.customer.dto.response.ApiResponse;
import com.thalicloud.customer.security.CustomerPrincipal;
import com.thalicloud.customer.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/customer/addresses")
@RequiredArgsConstructor
@Tag(name = "Customer Addresses", description = "Manage the authenticated customer's saved delivery addresses, including which one is the default.")
public class CustomerAddressController {

    private final AddressService addressService;

    @Operation(summary = "List saved addresses",
            description = "Returns all delivery addresses saved by the authenticated customer (identified by the JWT-bound phone number), including which one is marked as default.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal CustomerPrincipal principal) {
        log.info("getAddresses: start, phone={}", principal.getPhone());
        try {
            List<AddressResponse> addresses = addressService.getAddresses(principal.getPhone());
            ResponseEntity<ApiResponse<List<AddressResponse>>> response =
                    ResponseEntity.ok(ApiResponse.success("Addresses retrieved", addresses));
            log.info("getAddresses: end, phone={}", principal.getPhone());
            return response;
        } catch (Exception e) {
            log.error("getAddresses: failed, phone={}", principal.getPhone(), e);
            throw e;
        }
    }

    @Operation(summary = "Add a new address",
            description = "Validates and saves a new delivery address (label, flat/street/area/city, 6-digit pin code, optional lat/lng) for the authenticated customer. Returns HTTP 201 with the created address.")
    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @Valid @RequestBody AddressRequest request) {
        log.info("addAddress: start, phone={}", principal.getPhone());
        try {
            AddressResponse address = addressService.addAddress(principal.getPhone(), request);
            ResponseEntity<ApiResponse<AddressResponse>> response = ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Address added", address));
            log.info("addAddress: end, phone={}", principal.getPhone());
            return response;
        } catch (Exception e) {
            log.error("addAddress: failed, phone={}", principal.getPhone(), e);
            throw e;
        }
    }

    @Operation(summary = "Update an existing address",
            description = "Replaces the fields of an existing address (identified by its UUID) belonging to the authenticated customer with the validated values in the request body.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody AddressRequest request) {
        log.info("updateAddress: start, phone={}, addressId={}", principal.getPhone(), id);
        try {
            AddressResponse address = addressService.updateAddress(principal.getPhone(), id, request);
            ResponseEntity<ApiResponse<AddressResponse>> response =
                    ResponseEntity.ok(ApiResponse.success("Address updated", address));
            log.info("updateAddress: end, phone={}, addressId={}", principal.getPhone(), id);
            return response;
        } catch (Exception e) {
            log.error("updateAddress: failed, phone={}, addressId={}", principal.getPhone(), id, e);
            throw e;
        }
    }

    @Operation(summary = "Delete an address",
            description = "Permanently removes the address identified by its UUID from the authenticated customer's saved addresses.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID id) {
        log.info("deleteAddress: start, phone={}, addressId={}", principal.getPhone(), id);
        try {
            addressService.deleteAddress(principal.getPhone(), id);
            ResponseEntity<ApiResponse<Void>> response = ResponseEntity.ok(ApiResponse.success("Address deleted"));
            log.info("deleteAddress: end, phone={}, addressId={}", principal.getPhone(), id);
            return response;
        } catch (Exception e) {
            log.error("deleteAddress: failed, phone={}, addressId={}", principal.getPhone(), id, e);
            throw e;
        }
    }

    @Operation(summary = "Set an address as default",
            description = "Marks the address identified by its UUID as the authenticated customer's default delivery address, unsetting any previously default address.")
    @PostMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID id) {
        log.info("setDefaultAddress: start, phone={}, addressId={}", principal.getPhone(), id);
        try {
            AddressResponse address = addressService.setDefaultAddress(principal.getPhone(), id);
            ResponseEntity<ApiResponse<AddressResponse>> response =
                    ResponseEntity.ok(ApiResponse.success("Default address updated", address));
            log.info("setDefaultAddress: end, phone={}, addressId={}", principal.getPhone(), id);
            return response;
        } catch (Exception e) {
            log.error("setDefaultAddress: failed, phone={}, addressId={}", principal.getPhone(), id, e);
            throw e;
        }
    }
}
