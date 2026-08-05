package com.thalicloud.customer.controller;

import com.thalicloud.customer.dto.response.ApiResponse;
import com.thalicloud.customer.dto.response.FavouriteResponse;
import com.thalicloud.customer.security.CustomerPrincipal;
import com.thalicloud.customer.service.FavouriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/customer/favourites")
@RequiredArgsConstructor
@Tag(name = "Customer Favourites", description = "Manage the authenticated customer's list of favourite kitchens/vendors.")
public class CustomerFavouriteController {

    private final FavouriteService favouriteService;

    @Operation(summary = "List favourite kitchens",
            description = "Returns all kitchens/vendors the authenticated customer has marked as a favourite.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FavouriteResponse>>> getFavourites(
            @AuthenticationPrincipal CustomerPrincipal principal) {
        log.info("getFavourites: start, phone={}", principal.getPhone());
        try {
            List<FavouriteResponse> favourites = favouriteService.getFavourites(principal.getPhone());
            ResponseEntity<ApiResponse<List<FavouriteResponse>>> response =
                    ResponseEntity.ok(ApiResponse.success("Favourites retrieved", favourites));
            log.info("getFavourites: end, phone={}", principal.getPhone());
            return response;
        } catch (Exception e) {
            log.error("getFavourites: failed, phone={}", principal.getPhone(), e);
            throw e;
        }
    }

    @Operation(summary = "Add a kitchen to favourites",
            description = "Marks the kitchen identified by kitchenId as a favourite of the authenticated customer. Returns HTTP 201 with the created favourite entry.")
    @PostMapping("/{kitchenId}")
    public ResponseEntity<ApiResponse<FavouriteResponse>> addFavourite(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID kitchenId) {
        log.info("addFavourite: start, phone={}, kitchenId={}", principal.getPhone(), kitchenId);
        try {
            FavouriteResponse favourite = favouriteService.addFavourite(principal.getPhone(), kitchenId);
            ResponseEntity<ApiResponse<FavouriteResponse>> response = ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Kitchen added to favourites", favourite));
            log.info("addFavourite: end, phone={}, kitchenId={}", principal.getPhone(), kitchenId);
            return response;
        } catch (Exception e) {
            log.error("addFavourite: failed, phone={}, kitchenId={}", principal.getPhone(), kitchenId, e);
            throw e;
        }
    }

    @Operation(summary = "Remove a kitchen from favourites",
            description = "Removes the kitchen identified by kitchenId from the authenticated customer's list of favourites.")
    @DeleteMapping("/{kitchenId}")
    public ResponseEntity<ApiResponse<Void>> removeFavourite(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID kitchenId) {
        log.info("removeFavourite: start, phone={}, kitchenId={}", principal.getPhone(), kitchenId);
        try {
            favouriteService.removeFavourite(principal.getPhone(), kitchenId);
            ResponseEntity<ApiResponse<Void>> response =
                    ResponseEntity.ok(ApiResponse.success("Kitchen removed from favourites"));
            log.info("removeFavourite: end, phone={}, kitchenId={}", principal.getPhone(), kitchenId);
            return response;
        } catch (Exception e) {
            log.error("removeFavourite: failed, phone={}, kitchenId={}", principal.getPhone(), kitchenId, e);
            throw e;
        }
    }
}
