package org.shelter.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.database.entity.MedicalRecord;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.entity.enums.PetStatus;
import org.shelter.app.database.repository.MedicalRecordRepository;
import org.shelter.app.database.repository.PetRepository;
import org.shelter.app.database.repository.UserRepository;
import org.shelter.app.dto.MedicalRecordCreateDto;
import org.shelter.app.dto.MedicalRecordReadDto;
import org.shelter.app.exception.PetFailedStatusException;
import org.shelter.app.mapper.MedicalRecordMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalRecordService {

    private final MedicalRecordMapper medicalRecordMapper;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Transactional
    public MedicalRecordReadDto petHealthCheck(MedicalRecordCreateDto medicalRecordCreateDto) {
        Pet pet = petRepository.findPetById((medicalRecordCreateDto.getPetId()))
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        if (pet.getStatus() == PetStatus.AVAILABLE || pet.getStatus() == PetStatus.ADOPTED) {
            throw new PetFailedStatusException("Pet should have NOT_AVAILABLE status");
        }

        MedicalRecord medicalRecord = MedicalRecord.builder()
                .pet(pet)
                .user(userRepository.findByEmail(securityContext()).orElseThrow(() -> new RuntimeException("User not authenticated")))
                .diagnosis(medicalRecordCreateDto.getDiagnosis())
                .treatment(medicalRecordCreateDto.getTreatment())
                .prescription(medicalRecordCreateDto.getPrescription())
                .isHealthy(medicalRecordCreateDto.getIsHealthy())
                .examinationDate(LocalDateTime.now())
                .build();
        medicalRecordRepository.saveAndFlush(medicalRecord);
        petRepository.save(pet);

        log.info("Medical record created for pet {}: {}", pet.getName(), medicalRecord);
        return medicalRecordMapper.toDto(medicalRecord);
    }

    public List<MedicalRecordReadDto> findAllMedicalRecordsByPetId(Long petId) {
        return medicalRecordRepository.findAllByPetId(petId)
                .stream()
                .map(medicalRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    private String securityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
