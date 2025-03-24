package org.shelter.app.database.repository;

import org.shelter.app.database.entity.AdoptionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, UUID> {

    @Query("SELECT ar FROM AdoptionRequest ar WHERE ar.user.id = :userId AND ar.adoptionStatus = 'IN_PROGRESS'")
    Optional<AdoptionRequest> findByUserId(Long userId);
}
