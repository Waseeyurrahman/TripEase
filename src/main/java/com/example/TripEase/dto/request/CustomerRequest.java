package com.example.TripEase.dto.request;

import com.example.TripEase.Enum.Gender;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = "Customer name cannot be empty")
    private String name;

    @NotNull(message = "Age is required")
    private int age;


    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String emailId;

    private Gender gender;
}
