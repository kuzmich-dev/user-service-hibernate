package org.example.controllers;

import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void testListUsers() throws Exception {
        List<UserDTO> users = List.of(new UserDTO(1L, "Ivan"), new UserDTO(2L, "Volodya"));
        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("users/list"));
    }

    @Test
    void testGetUserById() throws Exception {
        UserDTO user = new UserDTO(2L, "Volodya");
        when(userService.getById(2L)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("users/detail"));
    }

    @Test
    void testCreateUser() throws Exception {
        UserDTO createdUser = new UserDTO(3L, "NewUser");
        when(userService.create(Mockito.any())).thenReturn(createdUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "NewUser")
                        .param("email", "new@example.com")
                        .param("age", "25"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/3"));
    }

    @Test
    void testEditForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/2/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userForm"))
                .andExpect(model().attribute("id", 2L))
                .andExpect(view().name("users/edit"));
    }

    @Test
    void testUpdateUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "UpdatedName")
                        .param("email", "updated@example.com")
                        .param("age", "30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/2"));

        Mockito.verify(userService).update(Mockito.eq(2L), Mockito.any());
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/2/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        Mockito.verify(userService).delete(2L);
    }

}