package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        User saved = userRepository.save(user);
        log.info("Пользователь успешно создан: {}", saved.getName());
        return saved;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    log.info("Пользователь найден: {}", user.getName());
                    return user;
                })
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", id);
                    return new RuntimeException("Пользователь не найден");
                });
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    User updated = userRepository.save(user);
                    log.info("Пользователь обновлён: {}", updated);
                    return updated;
                })
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден для обновления", id);
                    return new RuntimeException("Пользователь не найден");
                });
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("Пользователь с ID {} не найден для удаления", id);
            throw new RuntimeException("Пользователь не найден");
        }
        userRepository.deleteById(id);
        log.info("Пользователь с ID {} успешно удалён", id);
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Получено {} пользователей", users.size());
        return users;
    }

}