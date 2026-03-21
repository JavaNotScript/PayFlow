package com.payflow.Auth_servce.util;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(@NotBlank @Email String email,
                                  @NotBlank @Size(min = 8) String password,
                                  @NotBlank @Size(min = 3,max = 100) String firstName,
                                  @NotBlank @Size(min = 8,max = 100) String lastName,
                                  @NotBlank String currency
) {
}
