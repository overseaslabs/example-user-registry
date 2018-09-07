package com.overseaslabs.examples.ureg.controller;

import com.overseaslabs.examples.ureg.MessagePublisher;
import com.overseaslabs.examples.ureg.entity.User;
import com.overseaslabs.examples.ureg.repository.UserRepository;
import com.overseaslabs.examples.utils.exception.ResourceConflictException;
import com.overseaslabs.examples.utils.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Exposes the microservice's API
 */
@RestController
public class ApiController {

    private UserRepository userRepository;
    private MessagePublisher messagePublisher;

    public ApiController(UserRepository userRepository, MessagePublisher messagePublisher) {
        this.userRepository = userRepository;
        this.messagePublisher = messagePublisher;
    }

    /**
     * Checks whether the user's email conflicts with the email of another user in the DB
     */
    private boolean conflicts(User user) {
        User conflicting = userRepository.findByEmail(user.getEmail());

        if (conflicting == null || user.getId() != null && user.getId().equals(conflicting.getId())) {
            //no user with the same email found or it's found but it's actually the same user as passed
            return false;
        }

        return user.getEmail().equals(conflicting.getEmail());
    }

    /**
     * Find the user
     *
     * @param id The ID of the user
     * @return The found user
     */
    @GetMapping("/users/{id}")
    public User get(@PathVariable Integer id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
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
    public User create(@Valid @RequestBody User user) throws ResourceConflictException {
        if (conflicts(user)) {
            throw new ResourceConflictException("The email " + user.getEmail() + " is already used");
        }

        User newUser = userRepository.save(user);
        messagePublisher.publish(newUser);
        return newUser;
    }

    /**
     * Update a user
     *
     * @param id   The ID of the user to update
     * @param user User data
     * @return The updated user
     */
    @PutMapping("/users/{id}")
    public User update(@PathVariable Integer id, @Valid @RequestBody User user) throws ResourceNotFoundException, ResourceConflictException {
        if (conflicts(user)) {
            throw new ResourceConflictException("The email " + user.getEmail() + " is already used");
        }

        return userRepository
                .findById(id)
                .map(e -> {
                    e.setEmail(user.getEmail())
                            .setFirstName(user.getFirstName())
                            .setLastName(user.getLastName());

                    return userRepository.save(e);
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User %d is not found", id)));
    }

    /**
     * Delete the user
     *
     * @param id The ID of the user to delete
     */
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) throws ResourceNotFoundException {
        userRepository.findById(id)
                .map(e -> {
                    userRepository.delete(e);
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User %d is not found", id)));
    }
}
