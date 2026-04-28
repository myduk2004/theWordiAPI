package theWordI.backend.domain.readingPlan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import theWordI.backend.domain.readingPlan.entity.ReadingPlanBook;

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


}
