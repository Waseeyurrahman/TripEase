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
public class BookingRequest {

    @NotBlank(message = "Pickup location is required")
   private String pickup;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Trip distance is required")
    @Positive(message = "Trip distance must be greater than zero")
    private double tripDistanceInKm;
}
