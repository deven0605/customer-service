package com.thalicloud.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thalicloud.customer.enums.AddressLabel;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressResponse {
    private UUID id;
    private AddressLabel label;
    private String flatNo;
    private String building;
    private String street;
    private String area;
    private String landmark;
    private String city;
    private String pinCode;
    private Double lat;
    private Double lng;
    private boolean defaultAddress;
}
