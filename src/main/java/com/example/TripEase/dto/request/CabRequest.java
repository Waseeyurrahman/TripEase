package com.example.TripEase.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CabRequest {
    @NotBlank(message = "Cab number is required")
    private String cabNumber;

    @NotBlank(message = "Cab type is required")
    private String model;

    @NotNull(message = "Fare per km is required")
    @Positive(message = "Fare per km must be positive")
    private double perKilometerRate;
}
