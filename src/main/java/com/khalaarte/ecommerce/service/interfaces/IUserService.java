package com.khalaarte.ecommerce.service.interfaces;

import com.khalaarte.ecommerce.dto.UserCreateDTO;
import com.khalaarte.ecommerce.dto.UserDTO;

import java.util.Optional;
import java.util.Set;

public interface IUserService {

    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByEmail(String email);
    Set<UserDTO> getAllUsers();

    UserDTO createUser(UserCreateDTO userCreateDTO);
    UserDTO updateUser(UserDTO user);

    void deleteUser(Long id);
}
