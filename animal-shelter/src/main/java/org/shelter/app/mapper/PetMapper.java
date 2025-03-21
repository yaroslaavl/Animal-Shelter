package org.shelter.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.entity.Species;
import org.shelter.app.dto.PetCreateEditDto;
import org.shelter.app.dto.PetReadDto;
import org.shelter.app.dto.SpeciesCreateEditDto;
import org.shelter.app.dto.SpeciesReadDto;


@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "id", ignore = true)
    Pet toEntity(PetCreateEditDto petCreateEditDto);

    @Mapping(source = "species.id", target = "speciesId")
    PetReadDto toDto(Pet pet);
}
