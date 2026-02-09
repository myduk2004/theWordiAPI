package theWordI.backend.api;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import theWordI.backend.domain.bible.dto.BibleVerseCreateRequest;
import theWordI.backend.domain.bible.dto.BibleVerseResponse;
import theWordI.backend.domain.bible.dto.BibleVerseUpdateRequest;
import theWordI.backend.domain.bible.dto.BibleVersionBookResponse;
import theWordI.backend.domain.bible.entity.BibleBook;
import theWordI.backend.domain.bible.entity.BibleVerse;
import theWordI.backend.domain.bible.service.BibleService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bible")
@RequiredArgsConstructor
@Slf4j
public class BibleController {

    private final BibleService bibleService;


    //성경버전코드, 북코드 모두 조회
    @GetMapping("/versions")
    public BibleVersionBookResponse getVersions()
    {
        return new BibleVersionBookResponse(bibleService.getVersionAll(),
                bibleService.getBookAll());
    }


    //모든 책 조회(성경버전 + 특정버전)
    @GetMapping("/books")
    public List<BibleBook> getBooks(@RequestParam String versionId)
    {
        return bibleService.getBooksByVersion(versionId);
    }


    //성경구절조회(성경버전 N개 + 책 + 장 + 구절)
    @GetMapping("/verses")
    public ResponseEntity<List<BibleVerseResponse>> getVerses(@RequestParam List<String> versionIds,
                                                                        @RequestParam int bookId,
                                                                        @RequestParam int chapter,
                                                                        @RequestParam(required = false) Integer verse)
    {
        return ResponseEntity.ok(bibleService.getVersesByVersions(
                versionIds, bookId, chapter, verse
        ));
    }

    //성경구절 등록
    @PostMapping("/verse")
    public ResponseEntity<Map<String, Long>> addVerse(@Valid @RequestBody BibleVerseCreateRequest dto)
    {

        Long verseId = bibleService.createVerse(dto);
        Map<String, Long> responseBody = Collections.singletonMap("verseId", verseId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseBody);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/verse/{verseId}")
    public void deleteVerse(@PathVariable Long verseId)
    {
        bibleService.deleteVerse(verseId);
    }

    //수정(사용안함) -- 추후 사용할 가능성있음
    @PutMapping("/verse")
    public ResponseEntity<Long> updateVerse(@Valid @RequestBody BibleVerseUpdateRequest dto)

    {
        Long verseId = bibleService.updateVerse(dto);
        return ResponseEntity.ok().body(verseId);
    } 

}
