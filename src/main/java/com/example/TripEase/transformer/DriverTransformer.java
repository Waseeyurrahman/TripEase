package com.example.TripEase.transformer;

import com.example.TripEase.dto.request.DriverRequest;
import com.example.TripEase.dto.response.DriverResponse;
import com.example.TripEase.model.Driver;

public class DriverTransformer {

    public static Driver driverRequestToDriver(DriverRequest driverRequest){
        return Driver.builder()
                .name(driverRequest.getName())
                .age(driverRequest.getAge())
                .emailId(driverRequest.getEmailId())
                .build();
    }

    public static DriverResponse driverToDriverResponse(Driver driver){
return DriverResponse.builder()
        .driverId(driver.getDriverId())
        .name(driver.getName())
        .age(driver.getAge())
        .emailId(driver.getEmailId())
        .build();
    }
}
