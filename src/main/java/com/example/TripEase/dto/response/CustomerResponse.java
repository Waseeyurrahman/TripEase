package com.example.TripEase.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomerResponse {

       private String name;

        private int age;

        private String emailId;
    }
