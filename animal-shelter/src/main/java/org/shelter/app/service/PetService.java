package org.shelter.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.database.entity.MedicalRecord;
import org.shelter.app.database.entity.enums.Gender;
import org.shelter.app.database.entity.enums.PetStatus;
import org.shelter.app.database.repository.MedicalRecordRepository;
import org.shelter.app.database.repository.SpeciesRepository;
import org.shelter.app.database.specification.CustomSpecifications;
import org.shelter.app.dto.PetCreateEditDto;
import org.shelter.app.dto.PetReadDto;
import org.shelter.app.exception.MedicalRecordNotFoundException;
import org.shelter.app.exception.PetAlreadyRegisteredException;
import org.shelter.app.exception.PetFailedStatusException;
import org.shelter.app.exception.PetNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.repository.PetRepository;
import org.shelter.app.mapper.PetMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final MedicalRecordRepository medicalRecordRepository;
    private final SpeciesRepository speciesRepository;

    @Transactional
    public PetReadDto addPet(PetCreateEditDto petCreateEditDto) {
        if (petRepository.findPetByName(petCreateEditDto.getName()).isPresent()) {
            throw new PetAlreadyRegisteredException("Pet already exists in a system");
        }

        Pet pet = Pet.builder()
                .species(speciesRepository.findById(petCreateEditDto.getSpeciesId()).orElseThrow(() -> new PetNotFoundException("Species not found")))
                .name(petCreateEditDto.getName())
                .breed(petCreateEditDto.getBreed())
                .age(petCreateEditDto.getAge())
                .gender(petCreateEditDto.getGender())
                .description(petCreateEditDto.getDescription())
                .status(PetStatus.NOT_AVAILABLE)
                .build();

        return petMapper.toDto(petRepository.save(pet));
    }

    @Transactional
    public boolean addPetToAdoptionList(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found"));

        if (pet.getStatus() == PetStatus.AVAILABLE) {
            throw new PetAlreadyRegisteredException("Pet already available");
        }

        MedicalRecord lastMedicalRecord = medicalRecordRepository.findLastMedicalRecord(pet.getId())
                .orElseThrow(() -> new MedicalRecordNotFoundException("Medical record not found"));

        if (lastMedicalRecord.getExaminationDate().plusDays(30).isAfter(LocalDateTime.now())
                && lastMedicalRecord.getIsHealthy() == Boolean.TRUE) {

            pet.setStatus(PetStatus.AVAILABLE);
            petRepository.save(pet);
            return true;
        }
        return false;
    }

    @Transactional
    public PetReadDto changePetStatus(Long id, String status) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet not found"));

        if (pet.getStatus().equals(PetStatus.ADOPTED)) {
            throw new PetFailedStatusException("Pet already adopted");
        }

        pet.setStatus(PetStatus.valueOf(status));
        return petMapper.toDto(petRepository.save(pet));
    }

    public PetReadDto getPet(Long id) {
        return petMapper.toDto(petRepository.findById(id).orElseThrow(() -> new PetNotFoundException("Pet not found")));
    }

    public List<PetReadDto> getPetsByFilters(String breed, List<PetStatus> statuses, List<Gender> genders, String speciesName) {
        Specification<Pet> specification =
                Specification
                        .where(CustomSpecifications.hasSpeciesName(speciesName))
                        .and(CustomSpecifications.hasBreed(breed))
                        .and(CustomSpecifications.hasStatus(statuses))
                        .and(CustomSpecifications.hasGender(genders));

        return petRepository.findAll(specification)
                .stream()
                .map(petMapper::toDto)
                .collect(Collectors.toList());

    }
}

