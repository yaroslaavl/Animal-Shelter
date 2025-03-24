package org.shelter.app.dto;

import lombok.Data;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.entity.User;
import org.shelter.app.database.entity.enums.AdoptionStatus;

import java.time.LocalDateTime;

@Data
public class AdoptionRequestCreateEditDto {

    private Long petId;

    private AdoptionStatus adoptionStatus;

    private LocalDateTime reviewDate;

    private LocalDateTime requestDate;
}
