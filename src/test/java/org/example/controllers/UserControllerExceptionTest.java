package org.example.controllers;

import org.example.assembler.UserModelAssembler;
import org.example.exceptions.ResourceNotFoundException;
import org.example.service.KafkaNotificationService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(org.example.exceptions.GlobalExceptionHandler.class)
class UserControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private KafkaNotificationService kafkaNotificationService;

    @MockitoBean
    private UserModelAssembler assembler;

    @Test
    void testUserNotFound() throws Exception {
        Mockito.when(userService.getById(99L))
                .thenThrow(new ResourceNotFoundException("User with ID 99 not found"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User with ID 99 not found"));
    }

    @Test
    void testIllegalArgumentException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Invalid input"))
                .when(userService).delete(123L);

        mockMvc.perform(delete("/api/users/123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid input"));
    }

    @Test
    void testUnhandledException() throws Exception {
        Mockito.when(userService.getById(1L))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Internal error"));
    }

}