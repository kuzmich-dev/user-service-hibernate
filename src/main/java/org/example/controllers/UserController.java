package org.example.controllers;

import com.example.core.dto.UserEventDTO;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserCreateUpdateDTO;
import org.example.dto.UserDTO;
import org.example.service.KafkaProducerService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> listUsers() {
        List<UserDTO> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.getById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserCreateUpdateDTO dto) {
        UserDTO created = userService.create(dto);
        UserEventDTO event = new UserEventDTO(dto.getEmail(), "CREATE");
        kafkaProducerService.sendUserEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody UserCreateUpdateDTO dto) {
        userService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserCreateUpdateDTO deletedUserDto = userService.delete(id);

        UserEventDTO event = new UserEventDTO(deletedUserDto.getEmail(), "DELETE");
        kafkaProducerService.sendUserEvent(event);

        return ResponseEntity.noContent().build();
    }

}