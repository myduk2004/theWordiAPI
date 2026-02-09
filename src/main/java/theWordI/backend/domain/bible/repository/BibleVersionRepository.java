package theWordI.backend.domain.bible.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import theWordI.backend.domain.bible.entity.BibleVersion;

import java.util.List;

public interface BibleVersionRepository extends JpaRepository<BibleVersion, String> {
    List<BibleVersion> findByIsUsedOrderByOrderNoAsc(Integer isUsed);

}
