package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.example.assembler.UserModelAssembler;
import org.example.dto.UserCreateUpdateDTO;
import org.example.dto.UserDTO;
import org.example.service.KafkaNotificationService;
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
@Tag(name = "User API", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;
    private final KafkaNotificationService kafkaNotificationService;
    private final UserModelAssembler assembler;

    @Operation(summary = "Получить список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
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

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> getUser(@PathVariable Long id) {
        UserDTO user = userService.getById(id);
        return ResponseEntity.ok(assembler.toModel(user));
    }

    @Operation(summary = "Создать нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserDTO>> create(@RequestBody UserCreateUpdateDTO userCreateUpdateDTO) {
        UserDTO created = userService.create(userCreateUpdateDTO);
        kafkaNotificationService.notifyUserCreated(userCreateUpdateDTO);
        EntityModel<UserDTO> model = assembler.toModel(created);
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).getUser(created.getId())).toUri())
                .body(model);
    }

    @Operation(summary = "Обновить пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody UserCreateUpdateDTO dto) {
        userService.update(id, dto);
        kafkaNotificationService.notifyUserUpdated(id, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var deletedUser = userService.delete(id);
        kafkaNotificationService.notifyUserDeleted(deletedUser);
        return ResponseEntity.noContent().build();
    }

}