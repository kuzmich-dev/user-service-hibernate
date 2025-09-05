package user;

import org.example.dao.UserDAO;
import org.example.entity.User;
import org.example.hibernateconfig.HibernateConfiguration;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoIT {
    private static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");

    private SessionFactory sessionFactory;
    private UserDAO userDAO;

    @BeforeAll
    void startContainer() {
        postgres.start();

        sessionFactory = HibernateConfiguration.buildSessionFactory(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @AfterAll
    void stopContainer() {
        postgres.stop();
    }

    @BeforeEach
    void setup() {
        userDAO = new UserDAO();
    }

    @Test
    void testCreateAndRead() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        userDAO.create(user);
        assertNotNull(user.getId());

        User fromDb = userDAO.read(user.getId());
        assertEquals("Test User", fromDb.getName());
        assertEquals("test@example.com", fromDb.getEmail());
    }

    @Test
    void testUpdate() {
        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");

        userDAO.create(user);

        user.setName("New Name");
        userDAO.update(user);

        User updated = userDAO.read(user.getId());
        assertEquals("New Name", updated.getName());
    }

    @Test
    void testDelete() {
        User user = new User();
        user.setName("Delete Me");
        user.setEmail("delete@example.com");

        userDAO.create(user);
        Long id = user.getId();

        userDAO.delete(id);

        User deleted = userDAO.read(id);
        assertNull(deleted);
    }

    @Test
    void testFindAll() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@example.com");

        userDAO.create(user1);
        userDAO.create(user2);

        List<User> users = userDAO.findAll();
        assertTrue(users.size() >= 2);
    }

}