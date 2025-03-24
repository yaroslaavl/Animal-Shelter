package org.shelter.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.shelter.app.database.entity.AdoptionRequest;
import org.shelter.app.database.entity.User;


import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateDto {

    private Long userId;

    private UUID adoptionRequestId;

    private String message;
}
