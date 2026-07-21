package theWordI.backend.api;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import theWordI.backend.domain.meditation.dto.MeditationCreateRequest;
import theWordI.backend.domain.meditation.dto.MeditationListResponse;
import theWordI.backend.domain.meditation.dto.MeditationUpdateRequest;
import theWordI.backend.domain.meditation.entity.Meditation;
import theWordI.backend.domain.meditation.service.MeditationService;
import theWordI.backend.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/meditations")
@RequiredArgsConstructor
@Slf4j
public class MeditationController {

    private final MeditationService service;


    @GetMapping
    public ResponseEntity<Slice<MeditationListResponse>> getMeditations(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    )
    {
        Slice<MeditationListResponse> response = service.getMyMeditations(title, startDate, endDate, pageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{meditationId}")
    public ResponseEntity<Meditation> findMyMeditation(@PathVariable Long meditationId) {
        Meditation meditation = service.getMyMeditation(meditationId);

        return ResponseEntity.ok().body(meditation);
    }


    @PostMapping("/create")
    public ResponseEntity<Map<String, Long>> createMeditation(@Valid @RequestBody MeditationCreateRequest dto) {
        Long meditationId = service.save(dto);
        Map<String, Long> result = Collections.singletonMap("meditationId", meditationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @PutMapping("/update")
    public ResponseEntity<Map<String, Long>> updateMeditation(@Valid @RequestBody MeditationUpdateRequest dto) {
        Long meditationId = service.update(dto);
        Map<String, Long> result = Collections.singletonMap("meditationId", meditationId);
        return ResponseEntity.ok().body(result);
    }



    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/delete/{meditationId}")
    public void deleteMeditation(@PathVariable Long meditationId) {
        service.delete(meditationId);
    }

}
