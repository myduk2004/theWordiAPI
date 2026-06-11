package theWordI.backend.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import theWordI.backend.domain.headerVerse.dto.HeaderVerseResponse;
import theWordI.backend.domain.headerVerse.entity.HeaderVerse;
import theWordI.backend.domain.headerVerse.service.HeaderVerseService;

import java.util.List;

@RestController
@RequestMapping("/headerVerse")
@RequiredArgsConstructor
public class HeaderVerseController {

    private final HeaderVerseService svc;

    //헤더 성경구절 조회
    @GetMapping
    public ResponseEntity<List<HeaderVerseResponse>> getVersions()
    {
        List<HeaderVerseResponse> response = svc.getdHeaderVerses();
        return ResponseEntity.ok(response);
    }

}
