package org.example.controllers;

import org.example.assembler.UserModelAssembler;
import org.example.dto.UserCreateUpdateDTO;
import org.example.dto.UserDTO;
import org.example.service.KafkaNotificationService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(org.example.exceptions.GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private KafkaNotificationService kafkaNotificationService;

    @MockitoBean
    private UserModelAssembler assembler;

    @Test
    void testListUsers() throws Exception {
        List<UserDTO> users = List.of(
                new UserDTO(1L, "Ivan"),
                new UserDTO(2L, "Volodya")
        );
        when(userService.getAll()).thenReturn(users);
        when(assembler.toModel(users.get(0)))
                .thenReturn(org.springframework.hateoas.EntityModel.of(users.get(0)));
        when(assembler.toModel(users.get(1)))
                .thenReturn(org.springframework.hateoas.EntityModel.of(users.get(1)));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.users[0].id").value(1))
                .andExpect(jsonPath("_embedded.users[0].name").value("Ivan"))
                .andExpect(jsonPath("_embedded.users[1].id").value(2))
                .andExpect(jsonPath("_embedded.users[1].name").value("Volodya"));
    }

    @Test
    void testGetUserById() throws Exception {
        UserDTO user = new UserDTO(2L, "Volodya");
        when(userService.getById(2L)).thenReturn(user);
        when(assembler.toModel(user))
                .thenReturn(org.springframework.hateoas.EntityModel.of(user));

        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(2))
                .andExpect(jsonPath("name").value("Volodya"));
    }

    @Test
    void testCreateUser() throws Exception {
        UserDTO createdUser = new UserDTO(3L, "NewUser");
        when(userService.create(Mockito.any(UserCreateUpdateDTO.class))).thenReturn(createdUser);
        when(assembler.toModel(createdUser))
                .thenReturn(org.springframework.hateoas.EntityModel.of(createdUser));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewUser\",\"email\":\"new@example.com\",\"age\":25}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/users/3"))
                .andExpect(jsonPath("id").value(3))
                .andExpect(jsonPath("name").value("NewUser"));

        Mockito.verify(kafkaNotificationService).notifyUserCreated(Mockito.any(UserCreateUpdateDTO.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedName\",\"email\":\"updated@example.com\",\"age\":30}"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).update(Mockito.eq(2L), Mockito.any(UserCreateUpdateDTO.class));
        Mockito.verify(kafkaNotificationService).notifyUserUpdated(Mockito.eq(2L), Mockito.any(UserCreateUpdateDTO.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        UserCreateUpdateDTO deletedUser = new UserCreateUpdateDTO("Deleted", "deleted@example.com", 40);
        when(userService.delete(2L)).thenReturn(deletedUser);

        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(2L);
        Mockito.verify(kafkaNotificationService).notifyUserDeleted(deletedUser);
    }

}