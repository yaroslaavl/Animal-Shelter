package org.shelter.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.dto.AdoptionRequestCreateEditDto;
import org.shelter.app.dto.AdoptionRequestReadDto;
import org.shelter.app.service.AdoptionRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/adoption-request")
public class AdoptionRequestController {

    private final AdoptionRequestService adoptionRequestService;

    @PostMapping("/send")
    public ResponseEntity<AdoptionRequestReadDto> sendRequest(@RequestBody AdoptionRequestCreateEditDto adoptionRequestCreateEditDto) {
        return ResponseEntity.ok(adoptionRequestService.sendRequest(adoptionRequestCreateEditDto));
    }
}
