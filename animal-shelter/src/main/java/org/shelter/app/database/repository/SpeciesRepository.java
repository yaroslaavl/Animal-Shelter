package org.shelter.app.database.repository;

import org.shelter.app.database.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long>, JpaSpecificationExecutor<Species> {

     Optional<Species> findByName(String name);
}
