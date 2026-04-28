package theWordI.backend.domain.readingPlan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import theWordI.backend.domain.readingPlan.entity.ReadLog;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ReadLogRepository  extends JpaRepository<ReadLog, Long> {

    boolean existsByUserIdAndPlanIdAndBookIdAndChapterNumBetween(@Param("userId") Long userId,
                                                                 @Param("planId") Long planId,
                                                                 @Param("bookId") Integer bookId,
                                                                 @Param("start")   Integer start,
                                                                 @Param("end")   Integer end);


    void deleteByUserIdAndPlanId(@Param("userId") Long userId, @Param("planId")  Long planId);

    void deleteByUserIdAndPlanIdAndPlanBookId(@Param("userId") Long userId,  @Param("planId")  Long planId, @Param("planBookId")  Long planBookId);


    void deleteByUserIdAndPlanLogId(@Param("userId") Long userId, @Param("planId")  Long planId);

}
