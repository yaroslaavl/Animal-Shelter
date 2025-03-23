package org.shelter.app.dto;

import lombok.Data;
import org.shelter.app.database.entity.enums.Gender;
import org.shelter.app.database.entity.Species;
import org.shelter.app.database.entity.enums.PetStatus;

import java.time.LocalDateTime;

@Data
public class PetCreateEditDto {

    private Long speciesId;

    private String breed;

    private String name;

    private Integer age;

    private Gender gender;

    private String description;

    private PetStatus status;

    private LocalDateTime createdAt;
}
