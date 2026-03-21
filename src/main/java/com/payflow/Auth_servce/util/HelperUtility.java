package com.payflow.Auth_servce.util;

import com.payflow.Auth_servce.dtos.UserDTO;
import com.payflow.Auth_servce.models.User;

public class HelperUtility {
    public static UserDTO convertToUserDTO(User createdUser) {
        return   new UserDTO(
                createdUser.getUserId(),
                createdUser.getEmail(),
                createdUser.getFirstName(),
                createdUser.getLastName(),
                createdUser.getRole().getRoleName().name(),
                createdUser.getCurrency()
        );
    }
}
