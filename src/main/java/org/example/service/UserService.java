package org.example.service;

import org.example.dao.UserDAO;
import org.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO = new UserDAO();

    public void createUser(User user) {
        try {
            userDAO.create(user);
            log.info("Пользователь успешно создан: {}", user.getName());
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", user.getName(), e);
            throw new RuntimeException("Не удалось создать пользователя", e);
        }
    }

    public User getUserById(Long id) {
        try {
            User user = userDAO.read(id);
            if (user == null) {
                log.warn("Пользователь с ID {} не найден", id);
                throw new RuntimeException("Пользователь не найден");
            }
            log.info("Пользователь найден: {}", user.getName());
            return user;
        } catch (Exception e) {
            log.error("Ошибка при получении пользователя с ID {}", id, e);
            throw new RuntimeException("Не удалось получить пользователя", e);
        }
    }

    public void updateUser(User user) {
        try {
            userDAO.update(user);
            log.info("Пользователь обновлён: {}", user);
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя: {}", user.getName(), e);
            throw new RuntimeException("Не удалось обновить пользователя", e);
        }
    }

    public void deleteUser(Long id) {
        try {
            User user = userDAO.read(id);
            if (user == null) {
                log.warn("Пользователь с ID {} не найден для удаления", id);
                throw new RuntimeException("Пользователь не найден");
            }
            userDAO.delete(id);
            log.info("Пользователь с ID {} успешно удалён", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя с ID {}", id, e);
            throw new RuntimeException("Не удалось удалить пользователя", e);
        }
    }

    public List<User> getAllUsers() {
        try {
            List<User> users = userDAO.findAll();
            log.info("Получено {} пользователей", users.size());
            return users;
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            throw new RuntimeException("Не удалось получить список пользователей", e);
        }
    }

}