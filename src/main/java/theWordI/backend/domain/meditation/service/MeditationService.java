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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeditationService {

    protected final MeditationRepository repository;
    protected  final MeditationVerseRepository verse_repository;

    //명상 기록 조회(1건)
    public Meditation getMyMeditation(Long meditationId)
    {
        return repository.findByMeditationIdAndUserId(meditationId, SecurityUtil.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 묵상정보가 없습니다."));
    }


    //명상 기록 모두 조회(N건)
    public Slice<MeditationListResponse> getMyMeditations(String title,
                                                        LocalDate startDt,
                                                        LocalDate endDt,
                                                        Pageable pageable)
    {
        List<MeditationListResponse> list = repository.findMeditationList(SecurityUtil.getUserId(),
                title,
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
        Meditation meditation = getMyMeditation(meditationId);

        meditation.update(dto.getMeditationDt(),
                dto.getTitle(),
                dto.getBibleText(),
                dto.getEtcText(),
                dto.getEtcSource(),
                dto.getText());

        Set<Long> oldSet = new HashSet<>(verse_repository.findVerseIdByMeditationId(meditationId));
        Set<Long> newSet = dto.getVerseIds() == null ? Collections.emptySet() : new HashSet<>(dto.getVerseIds());

        if (!oldSet.equals(newSet))
        {
            verse_repository.deleteByMeditationId(meditationId);
            verse_repository.flush();

            if (!newSet.isEmpty()) {
                List<MeditationVerse> verses = newSet.stream()
                        .map(verseId -> MeditationVerse.builder()
                                .meditationId(meditationId)
                                .verseId(verseId)
                                .build())
                        .toList();
                verse_repository.saveAll(verses);
            }
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

/*
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

    }*/
}
