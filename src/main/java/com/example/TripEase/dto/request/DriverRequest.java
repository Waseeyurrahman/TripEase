package com.example.TripEase.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DriverRequest {

    @NotBlank(message = "Driver name is required")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Customer must be at least 18 years old")
    private int age;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String emailId;
}
