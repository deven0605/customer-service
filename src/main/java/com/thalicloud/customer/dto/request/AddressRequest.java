package com.thalicloud.customer.dto.request;

import com.thalicloud.customer.enums.AddressLabel;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressRequest {

    @NotNull(message = "Label is required")
    private AddressLabel label;

    @NotBlank(message = "Flat / Door number is required")
    @Size(max = 50)
    private String flatNo;

    @Size(max = 100)
    private String building;

    @NotBlank(message = "Street is required")
    @Size(max = 150)
    private String street;

    @NotBlank(message = "Area is required")
    @Size(max = 100)
    private String area;

    @Size(max = 150)
    private String landmark;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "Pin code is required")
    @Pattern(regexp = "\\d{6}", message = "Pin code must be exactly 6 digits")
    private String pinCode;

    @DecimalMin(value = "-90.0",  message = "Invalid latitude")
    @DecimalMax(value = "90.0",   message = "Invalid latitude")
    private Double lat;

    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0",  message = "Invalid longitude")
    private Double lng;

    private boolean defaultAddress;
}
