package theWordI.backend.domain.bible.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.jpa.repository.Query;
import theWordI.backend.domain.bible.dto.BibleVerseRow;
import theWordI.backend.domain.bible.entity.BibleVerse;

import java.util.List;
import java.util.Optional;

public interface BibleVerseRepository extends JpaRepository<BibleVerse, Long> {


    @Query(""" 
            select new theWordI.backend.domain.bible.dto.BibleVerseRow(
            b.verseId,
            a.versionId,
            a.versionName,
            b.bookId,
            b.chapter,
            b.verse,
            b.text
            )
            from BibleVersion a
            join BibleVerse b on a.versionId = b.versionId
            where a.versionId in :versionIds
            	and b.bookId = :bookId
            	and b.chapter = :chapter
            	and (:verse is null OR b.verse >= :verse)
            order by a.orderNo, a.versionId, b.bookId, b.chapter, b.verse
            """)
    List<BibleVerseRow> findVerseByVersions(
            @Param("versionIds") List<String> versionIds,
            @Param("bookId") int bookId,
            @Param("chapter") int chapter,
            @Param("verse") Integer verse
    );


    //존재여부만 효율적으로 확인
    boolean existsByVersionIdAndBookIdAndChapterAndVerse(String versionId, Integer bookId, Integer chapter, Integer verse);



    Optional<BibleVerse> findByVerseId(Long verseId);

    Optional<BibleVerse> findByVersionIdAndBookIdAndChapterAndVerse(
            String versionId, Integer bookId, Integer chapter, Integer verse);


    @Modifying(clearAutomatically = true)
    int deleteByVerseIdAndRegId(Long verseId, Long regId);

}
