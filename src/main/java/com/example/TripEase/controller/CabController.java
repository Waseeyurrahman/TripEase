package com.example.TripEase.controller;

import com.example.TripEase.dto.request.CabRequest;
import com.example.TripEase.dto.response.CabResponse;
import com.example.TripEase.service.CabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cab")
public class CabController {

    @Autowired
    CabService cabService;

    @PostMapping("/register/driver/{driverid}")
    @PreAuthorize("hasRole('ADMIN')")
    public CabResponse registerCab(@RequestBody CabRequest cabRequest,
                                   @PathVariable("driverid") int driverId){
        return  cabService.registerCab(cabRequest, driverId);

    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<CabResponse> getAllCabs() {
        return cabService.getAllCabs();
    }
}
