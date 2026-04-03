package com.example.TripEase.service;

import com.example.TripEase.dto.request.CabRequest;
import com.example.TripEase.dto.response.CabResponse;
import com.example.TripEase.exception.DriverNotFoundException;
import com.example.TripEase.model.Cab;
import com.example.TripEase.model.Driver;
import com.example.TripEase.repository.CabRepository;
import com.example.TripEase.repository.DriverRepository;
import com.example.TripEase.transformer.CabTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CabService {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    CabRepository cabRepository;

    public CabResponse registerCab(CabRequest cabRequest, int driverId) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Invalid Driver Id!"));

        // Step 1: Create cab
        Cab cab = CabTransformer.cabRequestToCab(cabRequest);

        // Step 2: Save cab FIRST
        Cab savedCab = cabRepository.save(cab);

        // Step 3: Set relationship
        driver.setCab(savedCab);

        // Step 4: Save driver
        Driver savedDriver = driverRepository.save(driver);

        return CabTransformer.cabToCabResponse(savedCab, savedDriver);


    }

    public List<CabResponse> getAllCabs() {
        List<Cab> cabs = cabRepository.findAll();
        List<CabResponse> responses = new ArrayList<>();

        for (Cab cab : cabs) {
            responses.add(
                    CabTransformer.cabToCabResponse(cab, null)
            );
        }
        return responses;
    }
}
