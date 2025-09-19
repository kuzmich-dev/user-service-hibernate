package org.example.service;

import org.example.dto.UserEventDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, UserEventDTO> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, UserEventDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserEvent(UserEventDTO userEventDTO) {
        kafkaTemplate.send("user-events", userEventDTO);
    }
}