package org.shelter.app.service;

import lombok.RequiredArgsConstructor;
import org.shelter.app.database.entity.Species;
import org.shelter.app.database.repository.SpeciesRepository;
import org.shelter.app.database.specification.CustomSpecifications;
import org.shelter.app.dto.SpeciesCreateEditDto;
import org.shelter.app.dto.SpeciesReadDto;
import org.shelter.app.exception.SpeciesAlreadyExistsException;
import org.shelter.app.exception.SpeciesNotFoundException;
import org.shelter.app.mapper.SpeciesMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpeciesService {

    private final SpeciesRepository speciesRepository;
    private final SpeciesMapper speciesMapper;

    public List<SpeciesReadDto> findAllByKeyword(String keyword) {
        Specification<Species> specification =
                Specification.where(CustomSpecifications.hasName(keyword));

        return speciesRepository.findAll(specification)
                .stream()
                .map(speciesMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SpeciesReadDto addNewSpecies(SpeciesCreateEditDto speciesCreateEditDto) {
        if (speciesRepository.findByName(speciesCreateEditDto.getName()).isPresent()) {
            throw new SpeciesAlreadyExistsException("Species already exists with name: " + speciesCreateEditDto.getName());
        }
        Species species = speciesMapper.toEntity(speciesCreateEditDto);
        return speciesMapper.toDto(speciesRepository.save(species));
    }

    @Transactional
    public void deleteSpecies(Long id) {
        Species species = speciesRepository.findById(id)
                .orElseThrow(() -> new SpeciesNotFoundException("Species not found with id: " + id));

        speciesRepository.delete(species);
    }
}
