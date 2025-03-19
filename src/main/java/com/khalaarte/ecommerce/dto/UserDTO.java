package com.khalaarte.ecommerce.dto;

import lombok.*;

@RequiredArgsConstructor
@ToString
@Getter
@Setter

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
}
