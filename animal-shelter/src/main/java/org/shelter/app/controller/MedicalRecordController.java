package org.shelter.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.dto.MedicalRecordCreateDto;
import org.shelter.app.dto.MedicalRecordReadDto;
import org.shelter.app.service.MedicalRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/medical-record")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping("/create")
    public ResponseEntity<MedicalRecordReadDto> createMedicalRecord(@RequestBody @Valid MedicalRecordCreateDto medicalRecordCreateDto) {
        return ResponseEntity.ok(medicalRecordService.petHealthCheck(medicalRecordCreateDto));
    }

    @GetMapping("/all/{petId}")
    public ResponseEntity<List<MedicalRecordReadDto>> findAllByPetId(@PathVariable("petId") Long petId) {
        return ResponseEntity.ok(medicalRecordService.findAllMedicalRecordsByPetId(petId));
    }
}
