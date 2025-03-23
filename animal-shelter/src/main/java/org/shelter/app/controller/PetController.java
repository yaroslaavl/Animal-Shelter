package org.shelter.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.dto.PetCreateEditDto;
import org.shelter.app.dto.PetReadDto;
import org.shelter.app.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
