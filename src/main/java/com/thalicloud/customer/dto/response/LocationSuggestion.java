package com.thalicloud.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationSuggestion {
    private String placeId;
    private String name;
    private String subtitle;
    private Double latitude;
    private Double longitude;
}
