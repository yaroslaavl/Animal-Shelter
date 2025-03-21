package org.shelter.app.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.repository.PetRepository;
import org.shelter.app.dto.PetCreateEditDto;
import org.shelter.app.dto.PetFilter;
import org.shelter.app.dto.PetReadDto;
import org.shelter.app.mapper.PetMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;

    /*public Page<PetReadDto> findAll(PetFilter petFilter, Pageable pageable){
        var predicate = QPredicate.builder()
                .add(petFilter.species(),pet.species::eq)
                .add(petFilter.age(),pet.age::eq)
                .add(petFilter.gender(),pet.gender::eq)
                .add(petFilter.isAvailable(),pet.isAvailable::eq)
                .buildAnd();
        log.info("Filters: {} ",petFilter);
        return petRepository.findAll(predicate,pageable)
                .map(petMapper::toDto);
    }*/

    public Optional<Pet> findPetById(Long petId) {
        return petRepository.findById(petId);
    }

    /*@Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void updatePet(Long id, PetCreateEditDto petCreateEditDto) {
        Pet petToUpdate = petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet not found with id: " + id));
        petMapper.updateEntityFromDto(petCreateEditDto,petToUpdate);

        petRepository.saveAndFlush(petToUpdate);
    }*/

}

