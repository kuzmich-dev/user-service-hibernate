package org.example.service;

import com.example.core.dto.UserEventDTO;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserCreateUpdateDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaNotificationService {

    private final KafkaProducerService kafkaProducerService;

    public void notifyUserCreated(UserCreateUpdateDTO userCreateUpdateDTO) {
        UserEventDTO eventDTO = new UserEventDTO(userCreateUpdateDTO.getEmail(), "CREATE");
        kafkaProducerService.sendUserEvent(eventDTO);
    }

    public void notifyUserUpdated(Long id, UserCreateUpdateDTO dto) {
        UserEventDTO eventDTO = new UserEventDTO(dto.getEmail(), "UPDATE");
        kafkaProducerService.sendUserEvent(eventDTO);
    }

    public void notifyUserDeleted(UserCreateUpdateDTO userCreateUpdateDTO) {
        UserEventDTO eventDTO = new UserEventDTO(userCreateUpdateDTO.getEmail(), "DELETE");
        kafkaProducerService.sendUserEvent(eventDTO);
    }

}