package org.example;

import org.example.consoleapp.ConsoleApp;
import org.example.dao.UserDAO;
import org.example.service.UserService;

public class Main {
    public static void main(String[] args) {
        new ConsoleApp(new UserService(new UserDAO())).run();
    }

}