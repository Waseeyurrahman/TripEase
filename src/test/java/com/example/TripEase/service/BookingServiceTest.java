package com.example.TripEase.service;

import com.example.TripEase.dto.request.BookingRequest;
import com.example.TripEase.dto.response.BookingResponse;
import com.example.TripEase.model.*;
import com.example.TripEase.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CabRepository cabRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void shouldBookCabSuccessfully() {

        // Arrange
        Customer customer = new Customer();
        customer.setCustomerId(1L);

        Cab cab = new Cab();
        cab.setPerKilometerRate(10);

        Driver driver = new Driver();
        driver.setAvailable(true);
        driver.setCab(cab);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(driverRepository.findByAvailableTrue())
                .thenReturn(List.of(driver));

        when(bookingRepository.save(org.mockito.ArgumentMatchers.any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookingRequest request = new BookingRequest("A", "B", 10);

        // Act
        BookingResponse response = bookingService.bookCab(request, 1);

        // Assert
        assertNotNull(response);
    }
}