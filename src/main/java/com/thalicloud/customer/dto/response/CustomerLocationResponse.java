package com.thalicloud.customer.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerLocationResponse {
    private Double lat;
    private Double lng;
    private String address;
    private LocalDateTime updatedAt;
}
