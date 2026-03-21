package com.payflow.Auth_servce.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String userRole;
    private String currency;
}
