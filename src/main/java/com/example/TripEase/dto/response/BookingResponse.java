package com.example.TripEase.dto.response;

import com.example.TripEase.Enum.TripStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BookingResponse {

    String pickup;

    String destination;

    double tripDistanceInKm;

    TripStatus tripStatus;

    double billAmount;

    Date bookedAt;

    Date lastUpdateAt;

    CustomerResponse customer;

    CabResponse cab;
}
