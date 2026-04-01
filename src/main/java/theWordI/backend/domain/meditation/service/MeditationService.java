package theWordI.backend.domain.meditation.service;


import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import theWordI.backend.domain.meditation.dto.MeditationCreateRequest;
import theWordI.backend.domain.meditation.dto.MeditationListResponse;
import theWordI.backend.domain.meditation.dto.MeditationListResponse2;
import theWordI.backend.domain.meditation.dto.MeditationUpdateRequest;
import theWordI.backend.domain.meditation.entity.Meditation;
import theWordI.backend.domain.meditation.entity.MeditationVerse;
import theWordI.backend.domain.meditation.repository.MeditationRepository;
import theWordI.backend.domain.meditation.repository.MeditationVerseRepository;
import theWordI.backend.util.SecurityUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeditationService {

    protected final MeditationRepository repository;
    protected  final MeditationVerseRepository verse_repository;

    @Transactional
    public Long save(MeditationCreateRequest dto)
    {
        Meditation meditation = repository.save(dto.toEntity(SecurityUtil.getUserId()));
        Long meditationId = meditation.getMeditationId();
        List<Long> verseIds = dto.getVerseIds();

        if (verseIds != null && !verseIds.isEmpty()) {
            List<MeditationVerse> verses = IntStream.range(0, verseIds.size())
                    .mapToObj(i -> MeditationVerse.builder()
                            .meditationId(meditationId)
                            .verseId(verseIds.get(i))
                            .orderNo((short) (i + 1))
                            .build())
                    .collect(Collectors.toList());
            verse_repository.saveAll(verses);
        }
        return meditationId;
    }

    @Transactional
    public Long update(MeditationUpdateRequest dto)
    {
        Long meditationId = dto.getMeditationId();
        Meditation meditation = repository.findById(meditationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 묵상정보가 없습니다."));

        if (!meditation.isOwner(SecurityUtil.getUserId()))
        {
            throw new AccessDeniedException("수정 권한이 없습니다");
        }

        meditation.update(dto.getMeditationDt(),
                dto.getTitle(),
                dto.getText());


        verse_repository.deleteByMeditationId(meditationId);
        verse_repository.flush();

        List<Long> verseIds = dto.getVerseIds();
        if (verseIds != null && !verseIds.isEmpty()) {
            List<MeditationVerse> verses = IntStream.range(0, verseIds.size())
                    .mapToObj(i -> MeditationVerse.builder()
                            .meditationId(meditationId)
                            .verseId(verseIds.get(i))
                            .orderNo((short) (i + 1))
                            .build())
                    .collect(Collectors.toList());

            verse_repository.saveAll(verses);
        }
        return meditation.getMeditationId();
    }

    @Transactional
    public void delete(Long meditationId)
    {
        repository.deleteByMeditationIdAndUserId(
                meditationId,
                SecurityUtil.getUserId());
    }

    //특정 유저의 특정 명상 기록 조회
    public Optional<Meditation> getMeditation(Long meditationId)
    {
        return repository.findByMeditationIdAndUserId(meditationId, SecurityUtil.getUserId());
    }

    //특정 유저의  명상 기록 모두 조회
//    public Slice<MeditationListResponse> getMeditations(String searchItem,
//                                                        String keyword,
//                                                        LocalDate startDt,
//                                                        LocalDate endDt,
//                                                        Pageable pageable)
//    {
//        return repository.searchMeditations(SecurityUtil.getUserId(),
//                searchItem,
//                keyword,
//                startDt,
//                endDt,
//                pageable);
//
//    }


    //특정 유저의  명상 기록 모두 조회
    public Slice<MeditationListResponse> getMeditations(String title,
                                                        String text,
                                                        LocalDate startDt,
                                                        LocalDate endDt,
                                                        Pageable pageable)
    {
        List<MeditationListResponse> list = repository.findMeditationList(SecurityUtil.getUserId(),
                title,
                text,
                startDt,
                endDt,
                pageable.getPageSize() + 1,
                pageable.getOffset()
        );

        boolean hasNext = list.size() > pageable.getPageSize();
        List<MeditationListResponse> results = (hasNext) ?
                list.subList(0, pageable.getPageSize()) : list;


        return new SliceImpl<>(results, pageable, hasNext);

    }
}
