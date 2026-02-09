package theWordI.backend.domain.bible.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import theWordI.backend.domain.bible.entity.BibleBook;
import theWordI.backend.domain.bible.entity.BibleBookId;

import java.util.List;

public interface BibleBookRepository extends JpaRepository<BibleBook, BibleBookId> {


    //versionId 로 Book 조회
    List<BibleBook> findByIdVersionIdOrderByIdBookId(String versionId);

    //versionId + testament 로 Book 조회
    List<BibleBook> findByIdVersionIdAndTestamentOrderByIdBookId(String versionId, String testament);
}

