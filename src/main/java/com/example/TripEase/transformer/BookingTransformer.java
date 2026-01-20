package com.example.TripEase.transformer;

import com.example.TripEase.Enum.TripStatus;
import com.example.TripEase.dto.request.BookingRequest;
import com.example.TripEase.dto.response.BookingResponse;
import com.example.TripEase.model.Booking;
import com.example.TripEase.model.Cab;
import com.example.TripEase.model.Customer;
import com.example.TripEase.model.Driver;

public class BookingTransformer {

    public  static Booking bookingRequestToBooking(BookingRequest bookingRequest, double perKilometerRate){
        return Booking.builder()
                .pickup(bookingRequest.getPickup())
                .destination(bookingRequest.getDestination())
                .tripDistanceInKm(bookingRequest.getTripDistanceInKm())
                .tripStatus(TripStatus.ONGOING)
                .billAmount(bookingRequest.getTripDistanceInKm()*perKilometerRate)
                .build();
    }

    public static BookingResponse bookingToBookingResponse(Booking booking, Customer customer, Cab cab, Driver driver){
        return BookingResponse.builder()
                .pickup(booking.getPickup())
                .destination(booking.getDestination())
                .tripDistanceInKm(booking.getTripDistanceInKm())
                .tripStatus(booking.getTripStatus())
                .billAmount(booking.getBillAmount())
                .bookedAt(booking.getBookedAt())
                .lastUpdateAt(booking.getLastUpdateAt())
                .customer(CustomerTransformer.customerToCustomerResponse(customer))
                .cab(CabTransformer.cabToCabResponse(cab,driver))
                .build();
    }
}
