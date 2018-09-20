package com.overseaslabs.examples.ureg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.overseaslabs.examples.ureg.controller.ApiController;
import com.overseaslabs.examples.ureg.entity.User;
import com.overseaslabs.examples.ureg.repository.UserRepository;
import com.overseaslabs.examples.utils.controller.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiController.class)
@EnableSpringDataWebSupport
class ApiControllerTest {
    //@Autowired
    MockMvc mvc;

    @Autowired
    ApiController apiController;

    @Autowired
    ObjectMapper om;

    @MockBean
    private UserRepository repository;

    @MockBean
    private MessagePublisher publisher;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .standaloneSetup(apiController)
                .setControllerAdvice(new Advice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testGetUser() throws Exception {
        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(new User()));
        MvcResult result = mvc.perform(
                MockMvcRequestBuilders
                        .get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void testFindUsers() throws Exception {
        Page<User> page = new PageImpl<>(Collections.singletonList(new User()));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        MvcResult result = mvc.perform(
                MockMvcRequestBuilders
                        .get("/users")
                        .param("page", "1")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void testCreateUser() throws Exception {
        User u = new User();
        u.setEmail("foo@bar.com")
                .setFirstName("foo")
                .setLastName("bar");

        when(repository.save(any(User.class))).thenReturn(u);

        MvcResult result = mvc.perform(
                MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u))
        ).andReturn();

        verify(repository, times(1)).save(any(User.class));
        verify(publisher, times(1)).publish(any(User.class));


        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
    }

    @Test
    void testCreateUserConflict() throws Exception {
        User u = new User();
        u.setId(1)
                .setEmail("foo@bar.com")
                .setFirstName("foo")
                .setLastName("bar");

        User conflicting = new User();

        conflicting.setId(2)
                .setEmail("foo@bar.com")
                .setFirstName("foo")
                .setLastName("bar");

        when(repository.findByEmail(eq("foo@bar.com"))).thenReturn(conflicting);

        mvc.perform(
                MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u))
        ).andExpect(status().isConflict());
    }

    @Test
    void testUpdateUser() throws Exception {
        User u = new User();
        u.setEmail("foo@bar.com")
                .setFirstName("foo")
                .setLastName("bar")
                .setId(1);

        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(u));
        when(repository.save(any(User.class))).thenReturn(u);

        MvcResult result = mvc.perform(
                MockMvcRequestBuilders
                        .put("/users/" + u.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u))
        ).andReturn();

        verify(repository, times(1)).findById(eq(u.getId()));
        verify(repository, times(1)).save(any(User.class));

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    void testUpdateUserConflict() throws Exception {
        User u = new User();
        u.setEmail("foo@bar.com")
                .setFirstName("foo")
                .setLastName("bar")
                .setId(1);

        User conflicting = new User();

        conflicting.setId(2)
                .setEmail("foo@bar.com")
                .setFirstName("foo")
                .setLastName("bar");


        when(repository.findByEmail(eq("foo@bar.com"))).thenReturn(conflicting);

        mvc.perform(
                MockMvcRequestBuilders
                        .put("/users/" + u.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u))
        ).andExpect(status().isConflict());
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        User u = new User();

        u.setEmail("foo@bar.com")
                .setFirstName("foo")
                .setLastName("bar")
                .setId(1);

        when(repository.findByEmail(eq("foo@bar.com"))).thenReturn(null);
        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());

        mvc.perform(
                MockMvcRequestBuilders
                        .put("/users/" + u.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u))
        ).andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        User u = new User();

        u.setEmail("foo@bar.com")
                .setFirstName("foo")
                .setLastName("bar")
                .setId(1);

        when(repository.findById(any(Integer.class))).thenReturn(Optional.of(u));

        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/users/" + u.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u))
        ).andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        User u = new User();

        u.setEmail("foo@bar.com")
                .setFirstName("foo")
                .setLastName("bar")
                .setId(1);

        when(repository.findById(any(Integer.class))).thenReturn(Optional.empty());

        mvc.perform(
                MockMvcRequestBuilders
                        .delete("/users/" + u.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(u))
        ).andExpect(status().isNotFound());
    }
}
