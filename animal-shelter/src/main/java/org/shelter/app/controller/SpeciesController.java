package org.shelter.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.dto.SpeciesCreateEditDto;
import org.shelter.app.dto.SpeciesReadDto;
import org.shelter.app.service.SpeciesService;
import org.shelter.app.validation.CreateAction;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/species")
@RequiredArgsConstructor
public class SpeciesController {

    private final SpeciesService speciesService;

    @PostMapping("/add")
    public ResponseEntity<SpeciesReadDto> add(@RequestBody @Validated(CreateAction.class)SpeciesCreateEditDto speciesCreateEditDto) {
        SpeciesReadDto speciesReadDto = speciesService.addNewSpecies(speciesCreateEditDto);

        return ResponseEntity.ok(speciesReadDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSpecies(@PathVariable("id") Long id) {
        speciesService.deleteSpecies(id);
        return ResponseEntity.ok("Deleted species");
    }

    @GetMapping("/dynamic-search")
    public ResponseEntity<List<SpeciesReadDto>> findByDynamicSearch(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(speciesService.findAllByKeyword(keyword));
    }
}
