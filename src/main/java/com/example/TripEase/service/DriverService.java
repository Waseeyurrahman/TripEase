package com.example.TripEase.service;

import com.example.TripEase.dto.request.DriverRequest;
import com.example.TripEase.dto.response.DriverResponse;
import com.example.TripEase.model.Driver;
import com.example.TripEase.repository.DriverRepository;
import com.example.TripEase.transformer.DriverTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DriverService {

    @Autowired
    DriverRepository driverRepository;

    public DriverResponse addDriver(DriverRequest driverRequest) {
        Driver driver = DriverTransformer.driverRequestToDriver(driverRequest);
        Driver savedDriver = driverRepository.save(driver);
        return DriverTransformer.driverToDriverResponse(savedDriver);


    }

    public List<DriverResponse> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        List<DriverResponse> responses = new ArrayList<>();

        for (Driver driver : drivers) {
            responses.add(
                    DriverTransformer.driverToDriverResponse(driver)
            );
        }
        return responses;
    }

    public DriverResponse getDriverById(int id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Invalid driver id"));

        return DriverTransformer.driverToDriverResponse(driver);
    }
    }

