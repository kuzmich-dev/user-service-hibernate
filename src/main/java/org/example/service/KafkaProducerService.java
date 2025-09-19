package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserEventDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, UserEventDTO> kafkaTemplate;

    public void sendUserEvent(UserEventDTO userEventDTO) {
        kafkaTemplate.send("user-events", userEventDTO);
    }

}