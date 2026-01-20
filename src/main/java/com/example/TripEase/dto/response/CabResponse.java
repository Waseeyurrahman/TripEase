package com.example.TripEase.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CabResponse {
    private String cabNumber;

    private String model;

    private double perKilometerRate;

    private boolean available;

    private DriverResponse driver;
}
