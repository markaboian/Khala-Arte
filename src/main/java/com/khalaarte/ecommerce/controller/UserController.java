package com.khalaarte.ecommerce.controller;

import com.khalaarte.ecommerce.dto.UserCreateDTO;
import com.khalaarte.ecommerce.dto.UserDTO;
import com.khalaarte.ecommerce.service.implementations.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Set<UserDTO>> getAllUsers() {
        Set<UserDTO> usersDTO = userService.getAllUsers();
        return usersDTO.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(usersDTO);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        UserDTO newUserDTO = userService.createUser(userCreateDTO);
        return new ResponseEntity<>(newUserDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        if (!id.equals(userDTO.getId())) {
            return ResponseEntity.badRequest().body(null);
        }
        else {
            UserDTO updatedUser = userService.updateUser(userDTO);
            return ResponseEntity.ok(updatedUser);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User with the id of: " + id + " was deleted successfully.", HttpStatus.ACCEPTED);
    }
}
