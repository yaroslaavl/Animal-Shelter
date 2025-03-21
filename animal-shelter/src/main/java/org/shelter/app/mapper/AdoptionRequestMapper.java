package org.shelter.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.shelter.app.database.entity.AdoptionRequest;
import org.shelter.app.database.entity.MedicalRecord;
import org.shelter.app.dto.AdoptionRequestReadDto;
import org.shelter.app.dto.MedicalRecordReadDto;

@Mapper(componentModel = "spring")
public interface AdoptionRequestMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "pet.id", target = "petId")
    AdoptionRequestReadDto toDto(AdoptionRequest adoptionRequest);
}
