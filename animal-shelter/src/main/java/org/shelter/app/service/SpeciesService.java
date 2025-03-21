package org.shelter.app.service;

import lombok.RequiredArgsConstructor;
import org.shelter.app.database.repository.SpeciesRepository;
import org.shelter.app.dto.SpeciesReadDto;
import org.shelter.app.mapper.SpeciesMapper;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeciesService {

    private final SpeciesRepository speciesRepository;
    private final SpeciesMapper speciesMapper;

    public List<SpeciesReadDto> findAll() {
        return speciesRepository.findAll().stream()
                .map(speciesMapper::toDto)
                .toList();
    }

}
