package com.overseaslabs.examples.userreg.controller;

import com.overseaslabs.examples.userreg.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the microservice's API
 */
@RestController
public class ApiController {

    /**
     * Find the user
     *
     * @param id The ID of the user
     * @return User instance
     */
    @GetMapping("/users/{id}")
    public User get(@PathVariable int id) {
        return new User();
    }

    /**
     * Find the users matching to the search request
     *
     * @param request HTTP request
     * @return The found users
     */
    @GetMapping("/users")
    public List<User> find(HttpServletRequest request) {
        return new ArrayList<>();
    }

    /**
     * Create a user
     *
     * @param request A request with data to create a user
     * @return An instance of the created user
     */
    @PostMapping("/users")
    public User create(HttpServletRequest request) {
        return new User();
    }

    /**
     * Update a user
     * @param request A request holding the data to update the user
     * @param id The ID of the user to update
     * @return An instance of the updated user
     */
    @PutMapping("/users/{id}")
    public User update(HttpServletRequest request, @PathVariable int id) {
        return new User();
    }

    /**
     * Delete the user
     * @param id The ID of the user to delete
     */
    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable int id) {

    }
}
