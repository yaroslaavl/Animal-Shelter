package org.shelter.app.database.repository;

import org.shelter.app.database.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet,Long> {

    @Query("SELECT p FROM Pet p " +
            "JOIN FETCH p.species WHERE p.id = :id")
    Optional<Pet> findById(@Param("id") Long id);

    Optional<Pet> findPetByName(String name);

    Optional<Pet> findPetById(Long id);
}