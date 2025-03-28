package org.shelter.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.dto.AdoptionRequestCreateEditDto;
import org.shelter.app.dto.AdoptionRequestReadDto;
import org.shelter.app.service.AdoptionRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PutMapping("/respond/{adoptionRequestId}")
    public ResponseEntity<String> respondAdoptionRequest(@PathVariable("adoptionRequestId") UUID adoptionRequestId,
                                                         @RequestParam("isAccepted") Boolean isAccepted) {
        try {
            adoptionRequestService.respondAdoptionRequest(adoptionRequestId, isAccepted);

            return ResponseEntity.ok("Adoption request accepted");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdoptionRequestReadDto>> getAllRequestsByUser() {
        return ResponseEntity.ok(adoptionRequestService.findAllByUserId());
    }

    @GetMapping("/id/{requestId}")
    public ResponseEntity<AdoptionRequestReadDto> getRequestById(@PathVariable("requestId") UUID requestId) {
        return ResponseEntity.ok(adoptionRequestService.findById(requestId));
    }
}
