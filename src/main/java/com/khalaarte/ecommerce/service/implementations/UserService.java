package com.khalaarte.ecommerce.service.implementations;

import com.khalaarte.ecommerce.dto.UserCreateDTO;
import com.khalaarte.ecommerce.dto.UserDTO;
import com.khalaarte.ecommerce.model.User;
import com.khalaarte.ecommerce.repository.IUserRepository;
import com.khalaarte.ecommerce.service.interfaces.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public Set<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toSet());
    }

    @Override
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        User user = modelMapper.map(userCreateDTO, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        if (userDTO.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update");
        }
        User existingUser = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        modelMapper.map(userDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
