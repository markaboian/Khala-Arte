package com.khalaarte.ecommerce.dto;

import com.khalaarte.ecommerce.model.User;
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

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.address = user.getAddress();
    }
}
