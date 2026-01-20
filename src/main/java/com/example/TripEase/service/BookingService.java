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

    @Autowired
    private JavaMailSender javaMailSender;


    public BookingResponse bookCab(BookingRequest bookingRequest, int customerId) {

        // 1. Validate customer
        Customer customer = customerRepository.findById(Long.valueOf(customerId) )
                .orElseThrow(() ->
                        new CustomerNotFoundException("Invalid Customer Id"));

        // 2. Find available cab
        Cab availableCab = cabRepository.getAvailableCabRandomly();
        if (availableCab == null) {
            throw new CabUnavailableException("Sorry! No cabs available");
        }

        // 3. Find driver for the cab
        Driver driver = driverRepository.getDriverByCabId(availableCab.getCabId());

        // 4. Create booking entity
        Booking booking = BookingTransformer.bookingRequestToBooking(
                bookingRequest,
                availableCab.getPerKilometerRate()
        );

        // 5. Set relationships (CRITICAL)
        booking.setCustomer(customer);
        booking.setCab(availableCab);
        booking.setDriver(driver);

        // 6. Save booking
        Booking savedBooking = bookingRepository.save(booking);

        // 7. Update cab availability
        availableCab.setAvailable(false);
        cabRepository.save(availableCab);

        // 8. Maintain bidirectional consistency
        customer.getBookings().add(savedBooking);
        driver.getBookings().add(savedBooking);

        customerRepository.save(customer);
        driverRepository.save(driver);

        // 9. Send confirmation email
        sendEmail(customer);

        // 10. Return response
        return BookingTransformer.bookingToBookingResponse(
                savedBooking,
                customer,
                availableCab,
                driver
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
    public void cancelBooking(int bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Invalid booking id"));

        // Make cab available again
        Cab cab = booking.getCab();
        cab.setAvailable(true);
        cabRepository.save(cab);

        // Remove booking
        bookingRepository.delete(booking);
    }

    /* -------------------------------------------------
       EMAIL NOTIFICATION
       ------------------------------------------------- */
    private void sendEmail(Customer customer) {

        String text = "Dear " + customer.getName() + ",\n\n"
                + "Your cab booking has been successfully confirmed.\n\n"
                + "Thank you for choosing TripEase. We hope you have a safe and pleasant journey.\n\n"
                + "Regards,\n"
                + "TripEase Team";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("noreply@tripease.com");
        mailMessage.setTo(customer.getEmailId());
        mailMessage.setSubject("TripEase Booking Confirmation");
        mailMessage.setText(text);

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
}
