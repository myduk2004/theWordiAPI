package theWordI.backend.domain.headerVerse.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import theWordI.backend.domain.headerVerse.dto.HeaderVerseResponse;
import theWordI.backend.domain.headerVerse.entity.HeaderVerse;

import java.util.List;

@Repository
public interface HeaderVerseRepository extends JpaRepository<HeaderVerse, Long> {

    @Query("""
            select new theWordI.backend.domain.headerVerse.dto.HeaderVerseResponse(
            h.headerVerseId,
            h.userId,
            h.versionId,
            v.versionName,
            h.bookId,
            b.nameKo,
            h.chapter,
            h.verse,
            verse.text,
            h.displayOrder
            )
            from HeaderVerse h
            join BibleVersion v on h.versionId=v.versionId
            join BibleBook b on h.versionId = b.id.versionId and h.bookId=b.id.bookId
            join BibleVerse verse on h.versionId=verse.versionId
             and h.bookId=verse.bookId
             and h.chapter=verse.chapter
             and h.verse=verse.verse
            where h.userId=:userId
            and h.activeYn='Y'
            order by h.displayOrder
            """)
    List<HeaderVerseResponse> findHeaderVerses(@Param("userId") Long userId);
}
