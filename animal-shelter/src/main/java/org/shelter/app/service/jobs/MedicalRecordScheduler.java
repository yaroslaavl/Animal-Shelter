package org.shelter.app.service.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.database.entity.MedicalRecord;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.entity.enums.PetStatus;
import org.shelter.app.database.repository.MedicalRecordRepository;
import org.shelter.app.database.repository.PetRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MedicalRecordScheduler {

    private final PetRepository petRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void checkExaminationDateOfMedicalRecord() {
        log.info("Checking examination date of MedicalRecord");

        List<MedicalRecord> latestMedicalRecordsForEachPet = medicalRecordRepository.findLatestMedicalRecordsForEachPet();
        List<MedicalRecord> recordsToSave = new ArrayList<>();
        List<Pet> petsToSave = new ArrayList<>();

        for (MedicalRecord medicalRecord : latestMedicalRecordsForEachPet) {
            if (medicalRecord.getExaminationDate().plusDays(30).isBefore(LocalDateTime.now())) {
                medicalRecord.setIsHealthy(Boolean.FALSE);
                medicalRecord.getPet().setStatus(PetStatus.NOT_AVAILABLE);
                recordsToSave.add(medicalRecord);
                petsToSave.add(medicalRecord.getPet());

                log.info("Updating medical record for pet {}: isHealthy=false, status=NOT_AVAILABLE", medicalRecord.getPet().getName());
            }
        }

        if (!recordsToSave.isEmpty()) {
            medicalRecordRepository.saveAll(recordsToSave);
            petRepository.saveAll(petsToSave);
            log.info("Updated {} medical records and pets", recordsToSave.size());
        } else {
            log.info("No medical records found");
        }
    }
}
