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
            log.info("Пользователь создан: {}", user);
        } catch (RuntimeException e) {
            log.error("Ошибка при создании пользователя", e);
        }
    }

    public User getUserById(Long id) {
        try {
            User user = userDAO.read(id);
            if (user == null) {
                log.warn("Пользователь с ID {} не найден", id);
            } else {
                log.info("Пользователь найден: {}", user);
            }
            return user;
        } catch (RuntimeException e) {
            log.error("Ошибка при получении пользователя", e);
            return null;
        }
    }

    public void updateUser(User user) {
        try {
            userDAO.update(user);
            log.info("Пользователь обновлён: {}", user);
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении пользователя", e);
        }
    }

    public void deleteUser(Long id) {
        try {
            userDAO.delete(id);
            log.info("Пользователь с ID {} удалён", id);
        } catch (RuntimeException e) {
            log.error("Ошибка при удалении пользователя", e);
        }
    }

    public List<User> getAllUsers() {
        try {
            List<User> users = userDAO.findAll();
            log.info("Получено {} пользователей", users.size());
            return users;
        } catch (RuntimeException e) {
            log.error("Ошибка при получении списка пользователей", e);
            return List.of();
        }
    }

}