package org.shelter.app.dto;

import lombok.Data;
import org.shelter.app.database.entity.AdoptionRequest;
import org.shelter.app.database.entity.User;


import java.time.LocalDateTime;

@Data
public class NotificationCreateEditDto {

    private User user;

    private AdoptionRequest adoptionRequest;

    private String message;

    private Boolean isRead;

    private LocalDateTime createdAt;
}
