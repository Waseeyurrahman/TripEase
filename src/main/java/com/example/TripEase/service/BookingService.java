package com.example.TripEase.service;

import com.example.TripEase.Enum.TripStatus;
import com.example.TripEase.dto.request.BookingRequest;
import com.example.TripEase.dto.response.BookingResponse;
import com.example.TripEase.exception.CabUnavailableException;
import com.example.TripEase.exception.CustomerNotFoundException;
import com.example.TripEase.model.Booking;
import com.example.TripEase.model.Cab;
import com.example.TripEase.model.Customer;
import com.example.TripEase.model.Driver;
import com.example.TripEase.repository.BookingRepository;
import com.example.TripEase.repository.CabRepository;
import com.example.TripEase.repository.CustomerRepository;
import com.example.TripEase.repository.DriverRepository;
import com.example.TripEase.transformer.BookingTransformer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CabRepository cabRepository;

    @Autowired(required = false)
    private JavaMailSender javaMailSender;


    @Transactional
    public BookingResponse bookCab(BookingRequest bookingRequest, int customerId) {

        // 1. Validate customer
        Customer customer = customerRepository.findById((long) customerId)        // ((long) customerId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Invalid Customer Id"));

        // 2. Get available drivers
        List<Driver> drivers = driverRepository.findByAvailableTrue();

        Driver selectedDriver = null;

        for (Driver d : drivers) {
            if (d.getCab() != null) {
                selectedDriver = d;
                break;
            }
        }

        if (selectedDriver == null) {
            throw new CabUnavailableException("No drivers with cab available");
        }

        Driver driver = selectedDriver;
        Cab cab = driver.getCab();

        System.out.println("Driver count: " + drivers.size());

        for (Driver d : drivers) {
            System.out.println("Driver: " + d.getDriverId() + " Cab: " + (d.getCab() != null));
        }

        // 4. Create booking
        Booking booking = Booking.builder()
                .pickup(bookingRequest.getPickup())
                .destination(bookingRequest.getDestination())
                .tripDistanceInKm(bookingRequest.getTripDistanceInKm())
                .tripStatus(TripStatus.ASSIGNED)
                .billAmount(bookingRequest.getTripDistanceInKm() * cab.getPerKilometerRate())
                .customer(customer)
                .driver(driver)
                .cab(cab)
                .build();

        // 5. Mark driver unavailable
        driver.setAvailable(false);

        // 6. Save data
        Booking savedBooking = bookingRepository.save(booking);
        driverRepository.save(driver);

        // 7. Send confirmation email
        sendEmail(customer);

        // 8. Return response
        return BookingTransformer.bookingToBookingResponse(
                savedBooking, customer, cab, driver
        );


    }

    /* -------------------------------------------------
       GET BOOKING BY ID
       ------------------------------------------------- */
    public BookingResponse getBookingById(int bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Invalid booking id"));

        return BookingTransformer.bookingToBookingResponse(
                booking,
                booking.getCustomer(),
                booking.getCab(),
                booking.getDriver()
        );
    }

    /* -------------------------------------------------
       CANCEL BOOKING
       ------------------------------------------------- */
    public BookingResponse cancelBooking(int bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Invalid booking id"));
        if (booking.getTripStatus() == TripStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed ride");
        }
        booking.setTripStatus(TripStatus.CANCELLED);

        // Free driver
        Driver driver = booking.getDriver();
        if (driver != null) {
            driver.setAvailable(true);
            driverRepository.save(driver);
        }

        Booking updated = bookingRepository.save(booking);

        return BookingTransformer.bookingToBookingResponse(
                updated,
                updated.getCustomer(),
                updated.getCab(),
                updated.getDriver()
        );
    }

    /* -------------------------------------------------
       EMAIL NOTIFICATION
       ------------------------------------------------- */
    private void sendEmail(Customer customer) {

        if (javaMailSender == null) return; // ✅ prevent crash

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(customer.getEmailId());
        mailMessage.setSubject("Booking Confirmed");
        mailMessage.setText("Your booking is confirmed");

        javaMailSender.send(mailMessage);
    }

    public List<BookingResponse> getBookingsByCustomer(int customerId) {
        Customer customer = customerRepository.findById(Long.valueOf(customerId))
                .orElseThrow(() ->
                        new CustomerNotFoundException("Invalid Customer Id"));

        List<BookingResponse> responses = new ArrayList<>();

        for (Booking booking : customer.getBookings()) {
            responses.add(
                    BookingTransformer.bookingToBookingResponse(
                            booking,
                            customer,
                            booking.getCab(),
                            booking.getDriver()
                    )
            );
        }

        return responses;
    }


    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingResponse> responses = new ArrayList<>();

        for (Booking booking : bookings) {
            responses.add(
                    BookingTransformer.bookingToBookingResponse(
                            booking,
                            booking.getCustomer(),
                            booking.getCab(),
                            booking.getDriver()
                    )
            );
        }

        return responses;
    }

    public BookingResponse updateBookingStatus(int bookingId, TripStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Invalid booking id"));

        booking.setTripStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);

        return BookingTransformer.bookingToBookingResponse(
                updatedBooking,
                updatedBooking.getCustomer(),
                updatedBooking.getCab(),
                updatedBooking.getDriver()
        );
    }
    /* -------------------------------------------------
   START RIDE
   ------------------------------------------------- */
    public BookingResponse startRide(int bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Invalid booking id"));

        if (booking.getTripStatus() != TripStatus.ASSIGNED) {
            throw new RuntimeException("Ride cannot be started");
        }

        booking.setTripStatus(TripStatus.ONGOING);

        Booking updated = bookingRepository.save(booking);

        return BookingTransformer.bookingToBookingResponse(
                updated,
                updated.getCustomer(),
                updated.getCab(),
                updated.getDriver()
        );
    }

    /* -------------------------------------------------
       COMPLETE RIDE
       ------------------------------------------------- */
    public BookingResponse completeRide(int bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Invalid booking id"));

        if (booking.getTripStatus() != TripStatus.ONGOING) {
            throw new RuntimeException("Ride not in progress");
        }

        booking.setTripStatus(TripStatus.COMPLETED);

        // Free driver
        Driver driver = booking.getDriver();
        driver.setAvailable(true);
        driverRepository.save(driver);

        Booking updated = bookingRepository.save(booking);

        return BookingTransformer.bookingToBookingResponse(
                updated,
                updated.getCustomer(),
                updated.getCab(),
                updated.getDriver()
        );
    }
}
