package org.shelter.app.dto;


import lombok.Value;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class NotificationReadDto {

    UUID id;

    Long userId;

    UUID adoptionRequestId;

    String message;

    Boolean isRead;

    LocalDateTime createdAt;
}
