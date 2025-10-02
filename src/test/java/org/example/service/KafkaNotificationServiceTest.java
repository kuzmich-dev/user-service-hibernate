package org.example.service;

import com.example.core.dto.UserEventDTO;
import org.example.dto.UserCreateUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class KafkaNotificationServiceTest {

    private KafkaProducerService kafkaProducerService;
    private KafkaNotificationService kafkaNotificationService;

    @BeforeEach
    void setUp() {
        kafkaProducerService = mock(KafkaProducerService.class);
        kafkaNotificationService = new KafkaNotificationService(kafkaProducerService);
    }

    @Test
    void notifyUserCreated_shouldSendCreateEvent() {
        UserCreateUpdateDTO dto = new UserCreateUpdateDTO("John", "test@example.com", 22);

        kafkaNotificationService.notifyUserCreated(dto);

        ArgumentCaptor<UserEventDTO> captor = ArgumentCaptor.forClass(UserEventDTO.class);
        verify(kafkaProducerService, times(1)).sendUserEvent(captor.capture());

        UserEventDTO event = captor.getValue();
        assertThat(event.getEmail()).isEqualTo("test@example.com");
        assertThat(event.getOperation()).isEqualTo("CREATE");
    }

    @Test
    void notifyUserUpdated_shouldSendUpdateEvent() {
        UserCreateUpdateDTO dto = new UserCreateUpdateDTO("Joan", "update@example.com", 22);

        kafkaNotificationService.notifyUserUpdated(1L, dto);

        ArgumentCaptor<UserEventDTO> captor = ArgumentCaptor.forClass(UserEventDTO.class);
        verify(kafkaProducerService, times(1)).sendUserEvent(captor.capture());

        UserEventDTO event = captor.getValue();
        assertThat(event.getEmail()).isEqualTo("update@example.com");
        assertThat(event.getOperation()).isEqualTo("UPDATE");
    }

    @Test
    void notifyUserDeleted_shouldSendDeleteEvent() {
        UserCreateUpdateDTO dto = new UserCreateUpdateDTO("Mike","delete@example.com", 33 );

        kafkaNotificationService.notifyUserDeleted(dto);

        ArgumentCaptor<UserEventDTO> captor = ArgumentCaptor.forClass(UserEventDTO.class);
        verify(kafkaProducerService, times(1)).sendUserEvent(captor.capture());

        UserEventDTO event = captor.getValue();
        assertThat(event.getEmail()).isEqualTo("delete@example.com");
        assertThat(event.getOperation()).isEqualTo("DELETE");
    }

}