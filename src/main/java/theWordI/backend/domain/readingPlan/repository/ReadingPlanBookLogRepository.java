package theWordI.backend.domain.readingPlan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import theWordI.backend.domain.readingPlan.entity.ReadingPlanBook;
import theWordI.backend.domain.readingPlan.entity.ReadingPlanBookLog;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingPlanBookLogRepository extends JpaRepository<ReadingPlanBookLog, Long> {

    List<ReadingPlanBookLog> findByUserIdAndPlanIdAndPlanBookIdOrderByStartChapter(@Param("userId") Long userId,
                                                                                   @Param("planId") Long planId,
                                                                                   @Param("planBookId") Long planBookId);


    void deleteByUserIdAndPlanId(@Param("userId") Long userId,
                        @Param("planId") Long planId);


    void deleteByUserIdAndPlanIdAndPlanBookId(@Param("userId") Long userId, @Param("planId") Long planId, @Param("planBookId")  Long planBookId);
}
