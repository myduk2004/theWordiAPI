package theWordI.backend.domain.bible.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import theWordI.backend.domain.bible.dto.*;
import theWordI.backend.domain.bible.entity.BibleBook;
import theWordI.backend.domain.bible.entity.BibleVerse;
import theWordI.backend.domain.bible.entity.BibleVersion;
import theWordI.backend.domain.bible.repository.BibleBookRepository;
import theWordI.backend.domain.bible.repository.BibleVerseRepository;
import theWordI.backend.domain.bible.repository.BibleVersionRepository;
import theWordI.backend.util.SecurityUtil;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BibleService {
    private final BibleVersionRepository versionRepository;
    private final BibleBookRepository bookRepository;
    private final BibleVerseRepository verseRepository;


    //모든 성경 버전 조회
    @Transactional(readOnly = true)
    public List<BibleVersion> getVersionAll()
    {
        return versionRepository.findByIsUsedOrderByOrderNoAsc(1);
    }


    //모든책 조회
    @Transactional(readOnly = true)
    public List<BibleBookResponse> getBookAll()
    {
        return bookRepository.findAll(
                Sort.by("id.VersionId").ascending()
                        .and(Sort.by("id.bookId").ascending())
        ).stream()
                .map(b -> new BibleBookResponse(
                        b.getId().getVersionId(),
                        b.getId().getBookId(),
                        b.getNameKo(),
                        b.getTestament(),
                        b.getChapterCount()
                )).toList();
    }


    //버전에 따른 모든책 조회
    @Transactional(readOnly = true)
    public List<BibleBook> getBooksByVersion(String versionId)
    {
        return bookRepository.findByIdVersionIdOrderByIdBookId(versionId);
    }



    //성경 내용 조회(버전 N개 + 책 + 장 + 구절 조건)
    public List<BibleVerseResponse> getVersesByVersions(List<String> versionIds,
                                                        int bookId,
                                                        int chapter,
                                                        Integer verse)
    {
        //1. DB조회
        List<BibleVerseRow> rows = verseRepository.findVerseByVersions(versionIds, bookId, chapter, verse);


        //2. versionId 기준 Grouping(순서유지)
        Map<String, List<BibleVerseRow>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        BibleVerseRow::getVersionId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));


        return grouped.values().stream()
                .map(versionRows -> {

                    if (versionRows.isEmpty()) {
                        return null;
                    }

                    BibleVerseRow first = versionRows.get(0);
                    BibleVersionResponse version = new BibleVersionResponse(
                            first.getVersionId(),
                            first.getVersionName()
                    );

                    List<BibleVerseItemResponse> verses =
                            versionRows.stream()
                                    .map(r -> new BibleVerseItemResponse(
                                            r.getVerseId(),
                                            r.getBookId(),
                                            r.getChapter(),
                                            r.getVerse(),
                                            r.getText()
                                    ))
                                    .toList();


                    return new BibleVerseResponse(version, verses);
                })
                .filter(Objects::nonNull)
                .toList();

    }


    //구절 저장/수정(버전 + 책)
    @Transactional
    public Long createVerse(BibleVerseCreateRequest dto)
    {
        //존재여부 체크
        if(verseRepository.existsByVersionIdAndBookIdAndChapterAndVerse(
                dto.getVersionId(),
                dto.getBookId(),
                dto.getChapter(),
                dto.getVerse()))
        {
            throw new IllegalArgumentException("이미 존재하는 성경 구절 정보입니다.");
        }

        //dto를 entity로 변환
        BibleVerse entity = dto.toEntity(SecurityUtil.getUserId());
        return verseRepository.save(entity).getVerseId();
    }

    @Transactional
    public Long updateVerse(BibleVerseUpdateRequest dto) throws AccessDeniedException
    {
        //존재여부 체크
        BibleVerse verse = verseRepository.findById(dto.getVerseId()).orElseThrow(
                ()->  new IllegalArgumentException("잘못된 성경정보입니다.")
         );

        Long userId = SecurityUtil.getUserId();
        if (!java.util.Objects.equals(verse.getRegId(), userId))
        {
            throw new AccessDeniedException("해당 성경 구절에 대한 수정/접근 권한이 없습니다.");
        }

        //builder 변경
        verse.updateText(dto.getText(), userId);


        // 명시적으로 save를 호출하여 변경 사항을 반영합니다.
        return verseRepository.save(verse).getVerseId();

    }

    //구절 삭제
    @Transactional
    public void deleteVerse(Long verseId)
    {
        int deleted = verseRepository.deleteByVerseIdAndRegId(verseId, SecurityUtil.getUserId());
        if (deleted == 0)
        {
            throw new AccessDeniedException("해당 성경 구절에 대한 삭제 권한이 없습니다.");
        }
    }

}
