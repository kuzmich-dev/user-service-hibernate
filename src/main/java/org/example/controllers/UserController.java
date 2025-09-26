package org.example.controllers;

import com.example.core.dto.UserEventDTO;
import lombok.RequiredArgsConstructor;
import org.example.assembler.UserModelAssembler;
import org.example.dto.UserCreateUpdateDTO;
import org.example.dto.UserDTO;
import org.example.service.KafkaProducerService;
import org.example.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;
    private final UserModelAssembler assembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> listUsers() {
        List<UserDTO> users = userService.getAll();

        List<EntityModel<UserDTO>> userModels = users.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserDTO>> collectionModel = CollectionModel.of(
                userModels,
                linkTo(methodOn(UserController.class).listUsers()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> getUser(@PathVariable Long id) {
        UserDTO user = userService.getById(id);
        return ResponseEntity.ok(assembler.toModel(user));
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserDTO>> create(@RequestBody UserCreateUpdateDTO dto) {
        UserDTO created = userService.create(dto);
        kafkaProducerService.sendUserEvent(new UserEventDTO(dto.getEmail(), "CREATE"));

        EntityModel<UserDTO> model = assembler.toModel(created);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).getUser(created.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody UserCreateUpdateDTO dto) {
        userService.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserCreateUpdateDTO deletedUser = userService.delete(id);
        kafkaProducerService.sendUserEvent(new UserEventDTO(deletedUser.getEmail(), "DELETE"));
        return ResponseEntity.noContent().build();
    }

}