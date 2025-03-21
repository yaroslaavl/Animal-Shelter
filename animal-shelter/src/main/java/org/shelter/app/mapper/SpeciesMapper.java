package org.shelter.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.shelter.app.database.entity.Species;
import org.shelter.app.dto.SpeciesCreateEditDto;
import org.shelter.app.dto.SpeciesReadDto;

@Mapper(componentModel = "spring")
public interface SpeciesMapper {

    @Mapping(target = "id", ignore = true)
    Species toEntity(SpeciesCreateEditDto speciesCreateEditDto);

    SpeciesReadDto toDto(Species species);
}
