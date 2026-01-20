package com.example.TripEase.controller;

import com.example.TripEase.dto.request.DriverRequest;
import com.example.TripEase.dto.response.DriverResponse;
import com.example.TripEase.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    DriverService driverService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public DriverResponse addDriver(@RequestBody DriverRequest driverRequest){
        return driverService.addDriver(driverRequest);

    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DriverResponse> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DriverResponse getDriverById(@PathVariable int id) {
        return driverService.getDriverById(id);
    }
}
