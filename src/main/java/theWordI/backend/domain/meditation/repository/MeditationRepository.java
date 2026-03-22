package theWordI.backend.domain.meditation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import theWordI.backend.domain.meditation.dto.MeditationListResponse;
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



}
