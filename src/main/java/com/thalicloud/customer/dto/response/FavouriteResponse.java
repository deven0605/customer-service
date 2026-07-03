package com.thalicloud.customer.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class FavouriteResponse {
    private UUID kitchenId;
    private LocalDateTime savedAt;
}
