package com.example.TripEase.transformer;

import com.example.TripEase.dto.request.CabRequest;
import com.example.TripEase.dto.response.CabResponse;
import com.example.TripEase.model.Cab;
import com.example.TripEase.model.Driver;

public class CabTransformer {

    public  static Cab cabRequestToCab(CabRequest cabRequest){
        return Cab.builder()
                .cabNumber(cabRequest.getCabNumber())
                .model(cabRequest.getModel())
                .perKilometerRate(cabRequest.getPerKilometerRate())
                .available(true)
                .build();
    }

    public static CabResponse cabToCabResponse(Cab cab, Driver driver){
        return CabResponse.builder()
                .cabNumber(cab.getCabNumber())
                .perKilometerRate(cab.getPerKilometerRate())
                .model(cab.getModel())
                .available(cab.isAvailable())
                .driver(DriverTransformer.driverToDriverResponse(driver))
                .build();
    }
}
