package service;

import org.example.dao.UserDAO;
import org.example.entity.User;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServiceTest {
    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        userService.createUser(user);

        verify(userDAO, times(1)).create(user);
    }

    @Test
    void testGetUserById_UserExists() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        when(userDAO.read(1L)).thenReturn(user);

        User result = userService.getUserById(1L);

        assertEquals("Test User", result.getName());
        verify(userDAO).read(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userDAO.read(1L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        assertEquals("Не удалось получить пользователя", ex.getMessage());
    }

}
