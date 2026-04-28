package theWordI.backend.domain.readingPlan.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import theWordI.backend.domain.readingPlan.entity.ReadingPlan;

import java.util.List;


@Repository
public interface ReadingPlanRepository extends JpaRepository<ReadingPlan, Long> {

    List<ReadingPlan> findByUserId(Long userId, Pageable pageable);
}
