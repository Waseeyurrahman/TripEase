package com.example.TripEase.controller;

import com.example.TripEase.Enum.TripStatus;
import com.example.TripEase.dto.request.BookingRequest;
import com.example.TripEase.dto.response.BookingResponse;
import com.example.TripEase.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    BookingService bookingService;


    @PostMapping("/book/customer/{customerid}")
    @PreAuthorize("hasRole('USER')")
    public BookingResponse bookCab(@Valid  @RequestBody BookingRequest bookibgRequest,
                                   @Positive(message = "Customer ID must be positive") @PathVariable("customerid") int customerId){
        return bookingService.bookCab(bookibgRequest,customerId);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('USER')")
    public BookingResponse getBooking(@PathVariable int id) {
        return bookingService.getBookingById(id);
    }

    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("hasRole('USER')")
    public String cancelBooking(@PathVariable int id) {
        bookingService.cancelBooking(id);
        return "Booking cancelled successfully";
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('USER')")
    public List<BookingResponse> getBookingsByCustomer(
            @PathVariable int customerId) {

        return bookingService.getBookingsByCustomer(customerId);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @PutMapping("/status/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookingResponse updateBookingStatus(
            @PathVariable int bookingId,
            @RequestParam TripStatus status) {

        return bookingService.updateBookingStatus(bookingId, status);
    }





}
