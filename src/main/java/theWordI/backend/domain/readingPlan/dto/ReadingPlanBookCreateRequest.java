package theWordI.backend.domain.readingPlan.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import theWordI.backend.domain.readingPlan.entity.PlanStatus;
import java.time.LocalDateTime;

@Getter
@Setter //테스트를 위해 잠시
@NoArgsConstructor //테스트용
public class ReadingPlanBookCreateRequest {

    private Long planBookId;

    private Integer bookId;

    private PlanStatus status = PlanStatus.WAITING;

    private int startChapter;

    private int endChapter;

    private LocalDateTime startDt;

    private LocalDateTime endDt;

}
