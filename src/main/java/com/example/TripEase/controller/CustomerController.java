package com.example.TripEase.controller;

import com.example.TripEase.Enum.Gender;
import com.example.TripEase.dto.request.CustomerRequest;
import com.example.TripEase.dto.response.CustomerResponse;
import com.example.TripEase.model.Customer;
import com.example.TripEase.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {


    @Autowired
    CustomerService customerService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public CustomerResponse addCustomer(@RequestBody CustomerRequest customerRequest){
        return customerService.addCustomer(customerRequest);
    }

    @GetMapping("/get/customer-id/{id}")
    @PreAuthorize("hasRole('USER')")
    public CustomerResponse getCustomer(@PathVariable("id") int customerId){
        return customerService.getCustomer(customerId);
    }

    @GetMapping("/get/gender/{gender}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerResponse> getAllByGender(@PathVariable("gender") Gender gender){
        return customerService.getAllByGender(gender);
    }

    //get all the people of a particular gender and age, ex- all males of age 25
    @GetMapping("/get")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerResponse> getAllByAgeAndGender(@RequestParam("gender") Gender gender,
                                                       @RequestParam("age") int age){
        return customerService.getAllByAgeAndGender(gender,age);
    }


    //get all the people of a particular gender and whose age > input age

    @GetMapping("/get-by-age-greater-than")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CustomerResponse> getAllByAgeAndGenderGreaterThan(@RequestParam("gender") Gender gender,
                                                       @RequestParam("age") int age) {
        return customerService.getAllByAgeAndGenderGreaterThan(gender, age);
    }
}
