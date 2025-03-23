package org.shelter.app.database.repository;

import org.shelter.app.database.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord,Long> {

    @Query("SELECT mr FROM MedicalRecord mr JOIN FETCH mr.pet WHERE mr.pet.status = 'AVAILABLE' AND mr.examinationDate = " +
            "(SELECT MAX(mr2.examinationDate) FROM MedicalRecord mr2 WHERE mr2.pet = mr.pet)")
    List<MedicalRecord> findLatestMedicalRecordsForEachPet();

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet.id = :petId AND mr.examinationDate = " +
            "(SELECT MAX(mr2.examinationDate) FROM MedicalRecord mr2 WHERE mr2.pet = mr.pet)")
    Optional<MedicalRecord> findLastMedicalRecord(@Param("petId") Long petId);

    List<MedicalRecord> findAllByPetId(Long petId);
}
