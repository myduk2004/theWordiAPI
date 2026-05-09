package theWordI.backend.domain.readingPlan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import theWordI.backend.domain.readingPlan.dto.ReadingPlanBookResponse;
import theWordI.backend.domain.readingPlan.entity.ReadingPlanBook;
import theWordI.backend.domain.readingPlan.entity.ReadingPlanBookLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ReadingPlanBookRepository extends JpaRepository<ReadingPlanBook, Long> {

    @Query(value = """
             select  COALESCE(MAX(chapter_num),0) as readNum from bible_read_log
             where user_id=:userId
              and plan_id=:planId
              and plan_book_id=:planBookId
            """, nativeQuery = true)
    long findReadChapterNum(@Param("userId") Long userId,@Param("planId") Long planId, @Param("planBookId") Long planBookId);

    Optional<ReadingPlanBook> findByUserIdAndPlanIdAndBookId(@Param("userId") Long userId,
                                            @Param("planId") Long planId,
                                            @Param("bookId") Integer bookId);

    boolean existsByUserIdAndPlanId(@Param("userId") Long userId,@Param("planId") Long planId);

    int countByUserIdAndPlanId(@Param("userId") Long userId,@Param("planId") Long planId);


    void deleteByUserIdAndPlanId(@Param("userId") Long userId,
                                 @Param("planId") Long planId);


    @Query("""
    SELECT new theWordI.backend.domain.readingPlan.dto.ReadingPlanBookResponse(
        a.planId, a.planBookId, a.bookId, a.readChaptersCnt, a.status, b.nameKo, b.chapterCount, a.startDt, a.endDt, max(c.chapterNum)
    )
    FROM ReadingPlanBook a
    JOIN BibleBook b ON a.bookId = b.id.bookId
    JOIN ReadLog c ON a.planBookId = c.planBookId
    WHERE a.userId = :userId
      AND a.planId = :planId
      AND b.id.versionId = :versionId
      GROUP BY a.planId, a.planBookId, a.bookId, a.readChaptersCnt,
                           a.status, b.nameKo, b.chapterCount, a.startDt, a.endDt
    ORDER BY a.bookId
    """)
    List<ReadingPlanBookResponse> findPlanBooks(@Param("userId") Long userId,
                                                   @Param("planId") Long planId,
                                                   @Param("versionId") String versionId);



}
