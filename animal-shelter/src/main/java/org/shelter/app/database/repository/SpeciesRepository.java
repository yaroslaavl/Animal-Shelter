package org.shelter.app.database.repository;

import org.shelter.app.database.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {

     @Query("SELECT s FROM Species s")
     List<Species> findAll();
}
