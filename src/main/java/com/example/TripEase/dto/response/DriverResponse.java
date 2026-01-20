package com.example.TripEase.dto.response;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder


public class DriverResponse {

    private int driverId;

    private String name;

    private int age;

    private String emailId;
}
