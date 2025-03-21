package org.shelter.app.dto;

import lombok.Value;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.entity.User;
import org.shelter.app.database.entity.enums.AdoptionStatus;

import java.time.LocalDateTime;

@Value
public class AdoptionRequestReadDto {

    Long userId;

    Long petId;

    String adoptionStatus;

    LocalDateTime reviewDate;

    LocalDateTime requestDate;
}