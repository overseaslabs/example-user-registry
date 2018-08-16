package com.overseaslabs.examples.userreg.controller;

import com.overseaslabs.examples.userreg.entity.User;
import com.overseaslabs.examples.userreg.exception.ResourceNotFoundException;
import com.overseaslabs.examples.userreg.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Exposes the microservice's API
 */
@RestController
public class ApiController {

    private UserRepository userRepository;

    public ApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find the user
     *
     * @param id The ID of the user
     * @return The found user
     */
    @GetMapping("/users/{id}")
    public Optional<User> get(@PathVariable Integer id) {
        return userRepository.findById(id);
    }

    /**
     * Find the users matching to the search request
     *
     * @return The found users
     */
    @GetMapping("/users")
    public Page<User> find(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Create a new user
     *
     * @param user User data
     * @return The created user
     */
    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) {
        return userRepository.save(user);
    }

    /**
     * Update a user
     *
     * @param id   The ID of the user to update
     * @param user User data
     * @return The updated user
     */
    @PutMapping("/users/{id}")
    public User update(@PathVariable Integer id, @Valid @RequestBody User user) {
        return userRepository.findById(id)
                .map(e -> {
                    e.setEmail(user.getEmail())
                            .setFirstName(user.getFirstName())
                            .setLastName(user.getLastName());

                    return userRepository.save(e);
                })
                .orElseThrow(() -> new ResourceNotFoundException("User" + id + " not found"));
    }

    /**
     * Delete the user
     *
     * @param id The ID of the user to delete
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(e -> {
                    userRepository.delete(e);
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("User" + id + " not found"));
    }
}
