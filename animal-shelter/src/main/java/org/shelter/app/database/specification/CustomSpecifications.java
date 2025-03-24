package org.shelter.app.database.specification;

import jakarta.persistence.criteria.Join;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.entity.Species;
import org.shelter.app.database.entity.enums.Gender;
import org.shelter.app.database.entity.enums.PetStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomSpecifications {

    public static Specification<Species> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("name")));
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), name.toLowerCase() + "%");
        };
    }

    public static Specification<Pet> hasSpeciesName(String speciesName) {
        return (root, query, criteriaBuilder) -> {
            Join<Pet, Species> species = root.join("species");
            return criteriaBuilder.like(criteriaBuilder.lower(species.get("name")), speciesName.toLowerCase() + "%");
        };
    }

    public static Specification<Pet> hasGender(List<Gender> genders) {
        return (root, query, criteriaBuilder) -> {
            if (genders == null) {
                return criteriaBuilder.conjunction();
            }
            return root.get("gender").in(genders);
        };
    }

    public static Specification<Pet> hasBreed(String breed) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("breed")), breed.toLowerCase() + "%");
    }

    public static Specification<Pet> hasStatus(List<PetStatus> petStatusList) {
        return (root, query, cb) -> root.get("status").in(petStatusList);
    }
}
