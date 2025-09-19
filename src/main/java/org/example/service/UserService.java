package org.example.service;

import org.example.dto.UserCreateUpdateDTO;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.exceptions.ResourceNotFoundException;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    public UserDTO getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found with id: " + id));
        return UserMapper.toDto(user);
    }

    public UserDTO create(UserCreateUpdateDTO userCreateUpdateDTO) {
        User user = UserMapper.fromDtoToCreate(userCreateUpdateDTO);
        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    public UserDTO update(Long id, UserCreateUpdateDTO userCreateUpdateDTO) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found with id: " + id));
        UserMapper.fromDtoToUpdate(userCreateUpdateDTO, user);
        User updated = userRepository.save(user);
        return UserMapper.toDto(updated);
    }

    public UserCreateUpdateDTO delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.deleteById(id);

        return new UserCreateUpdateDTO(
                user.getName(),
                user.getEmail(),
                user.getAge()
        );
    }

}