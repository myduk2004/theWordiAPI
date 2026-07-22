package theWordI.backend.domain.meditation.repository;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import theWordI.backend.domain.meditation.dto.MeditationListResponse;
import theWordI.backend.domain.meditation.dto.MeditationListResponse2;
import theWordI.backend.domain.meditation.entity.Meditation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface MeditationRepository  extends JpaRepository<Meditation, Long>, MeditationRepositoryCustom {

    //삭제
    void deleteByMeditationIdAndUserId(Long meditationId, Long userId);


    //특정 유저의  특정 명상  단건 조회
    Optional<Meditation> findByMeditationIdAndUserId(Long meditationId, Long userId);

    //특정 유저의 특정 날짜 명상 기록 복수건 조회
    List<Meditation> findByUserIdAndMeditationDt(Long userId, LocalDate meditationDt);


    @Query(value= """
            SELECT meditation_id as meditationId,
            title as title,
            bible_text as bibleText,
            etc_text as etcText,
            etc_source as etcSource,
            meditation_dt as meditationDt
            FROM meditation
            WHERE user_id = :userId
            AND (:title IS NULL OR title like  CONCAT('%', :title, '%'))
            AND (:bibleText IS NULL OR bible_text like  CONCAT('%', :bibleText, '%'))
            AND (:etcText IS NULL OR etc_text like  CONCAT('%', :etcText, '%'))
            AND (:startDt IS NULL OR meditation_dt >= :startDt)
            AND (:endDt IS NULL OR meditation_dt <= :endDt)
            ORDER BY meditation_dt DESC, meditation_id DESC
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<MeditationListResponse> findMeditationList(@Param("userId") Long userId,
                                                     @Param("title") String title,
                                                    @Param("bibleText") String bibleText,
                                                    @Param("etcText") String etcText,
                                                     @Param("startDt") LocalDate startDt,
                                                     @Param("endDt") LocalDate endDt,
                                                     @Param("limit") int limit,
                                                     @Param("offset") Long offset);
}
