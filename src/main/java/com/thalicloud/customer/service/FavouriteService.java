package com.thalicloud.customer.service;

import com.thalicloud.customer.dto.response.FavouriteResponse;

import java.util.List;
import java.util.UUID;

public interface FavouriteService {

    List<FavouriteResponse> getFavourites(String customerPhone);

    FavouriteResponse addFavourite(String customerPhone, UUID kitchenId);

    void removeFavourite(String customerPhone, UUID kitchenId);
}
