package org.example.controllers;

import org.example.dto.UserCreateUpdateDTO;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users/list";
    }

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        UserDTO user = userService.getById(id);
        model.addAttribute("user", user);
        return "users/detail";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("userForm", new UserCreateUpdateDTO());
        return "users/create";
    }

    @PostMapping
    public String create(@Validated @ModelAttribute("userForm") UserCreateUpdateDTO dto,
                         RedirectAttributes redirectAttributes) {
        UserDTO created = userService.create(dto);
        redirectAttributes.addFlashAttribute("message", "Пользователь создан!");
        return "redirect:/users/" + created.getId();
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        // Просто передаём пустой объект формы
        model.addAttribute("userForm", new UserCreateUpdateDTO());
        model.addAttribute("id", id);
        return "users/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Validated @ModelAttribute("userForm") UserCreateUpdateDTO dto,
                         RedirectAttributes redirectAttributes) {
        userService.update(id, dto);
        redirectAttributes.addFlashAttribute("message", "Пользователь обновлён!");
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Пользователь удалён!");
        return "redirect:/users";
    }

}

