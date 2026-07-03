package com.thalicloud.customer.controller;

import com.thalicloud.customer.dto.response.ApiResponse;
import com.thalicloud.customer.dto.response.FavouriteResponse;
import com.thalicloud.customer.security.CustomerPrincipal;
import com.thalicloud.customer.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer/favourites")
@RequiredArgsConstructor
public class CustomerFavouriteController {

    private final FavouriteService favouriteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FavouriteResponse>>> getFavourites(
            @AuthenticationPrincipal CustomerPrincipal principal) {

        List<FavouriteResponse> favourites = favouriteService.getFavourites(principal.getPhone());
        return ResponseEntity.ok(ApiResponse.success("Favourites retrieved", favourites));
    }

    @PostMapping("/{kitchenId}")
    public ResponseEntity<ApiResponse<FavouriteResponse>> addFavourite(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID kitchenId) {

        FavouriteResponse favourite = favouriteService.addFavourite(principal.getPhone(), kitchenId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Kitchen added to favourites", favourite));
    }

    @DeleteMapping("/{kitchenId}")
    public ResponseEntity<ApiResponse<Void>> removeFavourite(
            @AuthenticationPrincipal CustomerPrincipal principal,
            @PathVariable UUID kitchenId) {

        favouriteService.removeFavourite(principal.getPhone(), kitchenId);
        return ResponseEntity.ok(ApiResponse.success("Kitchen removed from favourites"));
    }
}
