package org.example.consoleapp;

import org.example.entity.User;
import org.example.service.UserService;

import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private final UserService userService = new UserService();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        System.out.println("Добро пожаловать в user-service!");

        while (true) {
            System.out.println("\nВведите команду: create, read, update, delete, list, exit");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "create" -> handleCreate();
                case "read" -> handleRead();
                case "update" -> handleUpdate();
                case "delete" -> handleDelete();
                case "list" -> handleList();
                case "exit" -> {
                    System.out.println("Завершение работы.");
                    return;
                }
                default -> System.out.println("Неизвестная команда.");
            }
        }
    }

    private void handleCreate() {
        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Возраст: ");
            int age = Integer.parseInt(scanner.nextLine());

            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setAge(age);

            userService.createUser(user);
            System.out.println("Пользователь создан.");
        } catch (Exception e) {
            System.out.println("Ошибка при создании пользователя.");
        }
    }

    private void handleRead() {
        try {
            System.out.print("ID: ");
            Long id = Long.parseLong(scanner.nextLine());
            User user = userService.getUserById(id);
            if (user != null) {
                System.out.println("Найден пользователь:");
                System.out.println(user);
            } else {
                System.out.println("Пользователь не найден.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при чтении.");
        }
    }

    private void handleUpdate() {
        try {
            System.out.print("ID: ");
            Long id = Long.parseLong(scanner.nextLine());
            User user = userService.getUserById(id);
            if (user == null) {
                System.out.println("Пользователь не найден.");
                return;
            }

            System.out.print("Новое имя: ");
            user.setName(scanner.nextLine());
            System.out.print("Новый email: ");
            user.setEmail(scanner.nextLine());
            System.out.print("Новый возраст: ");
            user.setAge(Integer.parseInt(scanner.nextLine()));

            userService.updateUser(user);
            System.out.println("Пользователь обновлён.");
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении.");
        }
    }

    private void handleDelete() {
        try {
            System.out.print("ID: ");
            Long id = Long.parseLong(scanner.nextLine());
            userService.deleteUser(id);
            System.out.println("Пользователь удалён.");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении.");
        }
    }

    private void handleList() {
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("Нет пользователей.");
                return;
            }

            System.out.printf("%-5s %-20s %-30s %-5s%n", "ID", "Имя", "Email", "Возраст");
            System.out.println("---------------------------------------------------------------");
            for (User user : users) {
                System.out.printf("%-5d %-20s %-30s %-5d%n",
                        user.getId(), user.getName(), user.getEmail(), user.getAge());
            }
        } catch (Exception e) {
            System.out.println("Ошибка при выводе списка пользователей.");
        }
    }

}