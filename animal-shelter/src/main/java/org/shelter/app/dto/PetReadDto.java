package org.shelter.app.dto;

import lombok.Value;
import org.shelter.app.database.entity.enums.Gender;
import org.shelter.app.database.entity.Species;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class PetReadDto {

    Long id;

    Long speciesId;

    String breed;

    String name;

    Integer age;

    String gender;

    List<MedicalRecordReadDto> medicalRecords;

    String description;

    String imageUrl;

    String status;

    LocalDateTime createdAt;
}
