package org.shelter.app.dto;


import org.shelter.app.database.entity.enums.Gender;
import org.shelter.app.database.entity.Species;

public record PetFilter(Species species,
                        Integer age,
                        Gender gender,
                        Boolean isAvailable) {
}
