package com.example.TripEase.service;

import com.example.TripEase.Enum.Gender;
import com.example.TripEase.dto.request.CustomerRequest;
import com.example.TripEase.dto.response.CustomerResponse;
import com.example.TripEase.model.Customer;
import com.example.TripEase.repository.CustomerRepository;
import com.example.TripEase.transformer.CustomerTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.TripEase.exception.CustomerNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    public CustomerResponse addCustomer(CustomerRequest customerRequest) {

        //RequestDTO -> Entity
        Customer customer = CustomerTransformer.customerRequestToCustomer(customerRequest);

        //save the entity to DB
        Customer savedCustomer = customerRepository.save(customer);

        //saved entity -> Response DTO
        return CustomerTransformer.customerToCustomerResponse(customer);
    }


    public CustomerResponse getCustomer(int
                                                customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(Long.valueOf(customerId));
        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException("Invalid customer id");
        }

        Customer savedCustomer = optionalCustomer.get();

        return CustomerTransformer.customerToCustomerResponse(savedCustomer);
    }


    public List<CustomerResponse> getAllByGender(Gender gender) {

        List<Customer> customers = customerRepository.findByGender(gender);

        //entity -> response dto
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for(Customer customer: customers){
            customerResponses.add(CustomerTransformer.customerToCustomerResponse(customer));

        }
        return customerResponses;
    }

    public List<CustomerResponse> getAllByAgeAndGender(Gender gender, int age) {
        List<Customer> customers = customerRepository.findByGenderAndAge(gender,age);
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for(Customer customer: customers){
            customerResponses.add(CustomerTransformer.customerToCustomerResponse(customer));

        }
        return customerResponses;
    }

    public List<CustomerResponse> getAllByAgeAndGenderGreaterThan(Gender gender, int age) {
        List<Customer> customers = customerRepository.getAllByAgeAndGenderGreaterThan(gender,age);
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for(Customer customer: customers){
            customerResponses.add(CustomerTransformer.customerToCustomerResponse(customer));

        }
        return customerResponses;
    }
}
