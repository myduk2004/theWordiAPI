package theWordI.backend.domain.meditation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import theWordI.backend.domain.meditation.entity.MeditationVerse;

import java.util.List;


public interface MeditationVerseRepository extends JpaRepository<MeditationVerse, Long> {

    void deleteByMeditationId(Long meditationId);

    @Query("""
            select mv.verseId
            from MeditationVerse mv
            where mv.meditationId = :meditationId
            """)
    List<Long> findVerseIdByMeditationId(Long meditationId);
}
