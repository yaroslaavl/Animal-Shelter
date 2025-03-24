package org.shelter.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ietf.jgss.GSSContext;
import org.shelter.app.database.entity.enums.Gender;
import org.shelter.app.database.entity.enums.PetStatus;
import org.shelter.app.dto.PetCreateEditDto;
import org.shelter.app.dto.PetReadDto;
import org.shelter.app.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet")
public class PetController {

    private final PetService petService;

    @PostMapping("/addNew")
    public ResponseEntity<PetReadDto> addNewPet(@RequestBody PetCreateEditDto petCreateEditDto) {
        return ResponseEntity.ok(petService.addPet(petCreateEditDto));
    }

    @PutMapping("/add-to-adoption-list/{petId}")
    public ResponseEntity<String> addPetToAdoptionList(@PathVariable("petId") Long petId) {
        boolean isAdded = petService.addPetToAdoptionList(petId);

        if (isAdded) {
            return ResponseEntity.ok("Pet added to adoption list");
        }
        return ResponseEntity.ok("Pet not added to adoption list");
    }

    @GetMapping("/all-statuses")
    public ResponseEntity<List<PetReadDto>> getFilteredPets(@RequestParam(value = "breed", required = false) String breed,
                                                            @RequestParam(value = "speciesName", required = false) String speciesName,
                                                            @RequestParam(value = "gender", required = false) String gender) {

        log.info("Admin method to find pets by filters. " +
                 "Breed: {}, SpeciesName: {}, Gender: {} ", breed, speciesName, gender);

        return ResponseEntity.ok(petService.getPetsByFilters(
                breed,
                List.of(PetStatus.ADOPTED, PetStatus.AVAILABLE, PetStatus.NOT_AVAILABLE),
                getGenders(gender),
                speciesName));
    }


    @GetMapping("/available")
    public ResponseEntity<List<PetReadDto>> getFilteredPetsForUsers(@RequestParam(value = "breed", required = false) String breed,
                                                                    @RequestParam(value = "speciesName", required = false) String speciesName,
                                                                    @RequestParam(value = "gender", required = false) String gender) {

        log.info("Pets with available status. " +
                "Breed: {}, SpeciesName: {}, Gender: {} ", breed, speciesName, gender);

        return ResponseEntity.ok(petService.getPetsByFilters(
                breed,
                List.of(PetStatus.AVAILABLE),
                getGenders(gender),
                speciesName));
    }

    private List<Gender> getGenders(String gender) {
        List<Gender> genders = new ArrayList<>();
        if (gender == null || gender.isBlank()) {
            genders = List.of(Gender.values());
        } else {
            try {
                genders = List.of(Gender.valueOf(gender.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid gender: {}, returning empty result", gender);
            }
        }
        return genders;
    }
}
