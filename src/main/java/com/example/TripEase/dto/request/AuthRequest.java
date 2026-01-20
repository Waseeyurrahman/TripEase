package com.example.TripEase.dto.request;

import com.example.TripEase.Enum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private Role role;


}
