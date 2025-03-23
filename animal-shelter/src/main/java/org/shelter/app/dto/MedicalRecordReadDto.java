package org.shelter.app.dto;

import lombok.Value;
import org.shelter.app.database.entity.Pet;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
public class MedicalRecordReadDto {

    Long id;

    Long petId;

    Long userId;

    String diagnosis;

    String treatment;

    String prescription;

    Boolean isHealthy;

    LocalDateTime examinationDate;
}
