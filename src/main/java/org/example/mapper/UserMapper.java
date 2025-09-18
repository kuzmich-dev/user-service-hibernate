package org.example.mapper;

import org.example.dto.UserCreateUpdateDTO;
import org.example.dto.UserDTO;
import org.example.entity.User;

public class UserMapper {

    public static UserDTO toDto(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }

    public static User fromDtoToCreate(UserCreateUpdateDTO createUpdateDTO) {
        User user = new User();
        user.setName(createUpdateDTO.getName());
        user.setEmail(createUpdateDTO.getEmail());
        user.setAge(createUpdateDTO.getAge());
        return user;
    }

    public static void fromDtoToUpdate(UserCreateUpdateDTO userCreateUpdateDTO, User user) {
        user.setName(userCreateUpdateDTO.getName());
        user.setEmail(userCreateUpdateDTO.getEmail());
        user.setAge(userCreateUpdateDTO.getAge());
    }

}