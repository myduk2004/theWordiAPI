package theWordI.backend.domain.readingPlan.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import theWordI.backend.domain.readingPlan.entity.ReadingPlan;

import java.util.List;


@Repository
public interface ReadingPlanRepository extends JpaRepository<ReadingPlan, Long> {

    List<ReadingPlan> findByUserId(Long userId, Pageable pageable);

    @Query("select MAX(r.readCount) FROM ReadingPlan r WHERE r.userId = :userId")
    int findMaxReadCount(@Param("userId") Long userId);


    @Modifying
    @Query("update ReadingPlan r SET r.readCount = r.readCount -1 WHERE r.userId = :userId And r.planId > :planId")
    void decrementReadCounts(@Param("userId") Long userId, @Param("planId") Long planId);

}
