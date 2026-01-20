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

        Optional<Driver> optionalDriver = driverRepository.findById(driverId);
        if(optionalDriver.isEmpty()){
            throw new DriverNotFoundException("Invalid Driver Id!");
        }

        Driver driver = optionalDriver.get();
        Cab cab = CabTransformer.cabRequestToCab(cabRequest);
        driver.setCab(cab);

        Driver savedDriver = driverRepository.save(driver);// save both driver and cab
        return CabTransformer.cabToCabResponse(savedDriver.getCab(),savedDriver);


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
