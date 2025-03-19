package com.khalaarte.ecommerce.dto;

import lombok.Data;

@Data
public class UserCreateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String address;
}
