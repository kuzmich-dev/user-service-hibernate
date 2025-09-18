package org.example.service;

import org.example.dto.UserCreateUpdateDTO;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.exceptions.ResourceNotFoundException;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetByIdSuccess() {
        User user = new User(1L, "Ivan", "ivan@example.com", 30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getName());
    }

    @Test
    void testGetByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    void testCreateUser() {
        UserCreateUpdateDTO dto = new UserCreateUpdateDTO("Volodya", "volodya@example.com", 25);
        User savedUser = new User(2L, "Volodya", "volodya@example.com", 25);

        when(userRepository.save(any())).thenReturn(savedUser);

        UserDTO result = userService.create(dto);

        assertEquals(2L, result.getId());
        assertEquals("Volodya", result.getName());
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User(3L, "OldName", "old@example.com", 40);
        UserCreateUpdateDTO dto = new UserCreateUpdateDTO("NewName", "new@example.com", 35);

        when(userRepository.findById(3L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(existingUser);

        assertDoesNotThrow(() -> userService.update(3L, dto));

        assertEquals("NewName", existingUser.getName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals(35, existingUser.getAge());
    }

    @Test
    void testDeleteUser() {
        when(userRepository.existsById(4L)).thenReturn(true);
        assertDoesNotThrow(() -> userService.delete(4L));
        verify(userRepository).deleteById(4L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(404L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(404L));
    }

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(
                new User(1L, "Ivan", "ivan@example.com", 30),
                new User(2L, "Volodya", "volodya@example.com", 25)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals("Ivan", result.get(0).getName());
    }

}