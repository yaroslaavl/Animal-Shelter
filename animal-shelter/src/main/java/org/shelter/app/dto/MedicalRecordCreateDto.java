package org.shelter.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.entity.User;

import java.time.LocalDateTime;

@Data
public class MedicalRecordCreateDto {

    @NotNull
    private Long petId;

    @NotBlank
    private String diagnosis;

    private String treatment;

    private String prescription;

    @NotNull
    private Boolean isHealthy;

    private LocalDateTime examinationDate;
}
