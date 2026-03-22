package theWordI.backend.domain.meditation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import theWordI.backend.domain.meditation.entity.MeditationVerse;

public interface MeditationVerseRepository extends JpaRepository<MeditationVerse, Long> {

    void deleteByMeditationId(Long meditationId);
}
