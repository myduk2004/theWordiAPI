package theWordI.backend.domain.readingPlan.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import theWordI.backend.domain.readingPlan.entity.PlanStatus;
import theWordI.backend.domain.readingPlan.entity.ReadingPlan;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReadingPlanResponse {

    private Long planId;
    private String versionId;
    private int readingCount;
    private String title;
    private LocalDate startDt;
    private LocalDateTime endDt;
    private PlanStatus status;

    public static ReadingPlanResponse from(ReadingPlan entity)
    {
        return ReadingPlanResponse.builder()
                .planId(entity.getPlanId())
                .versionId(entity.getVersionId())
                .readingCount(entity.getReadingCount())
                .title(entity.getTitle())
                .startDt(entity.getStartDt())
                .endDt(entity.getEndDt())
                .status(entity.getStatus())
                .build();
    }
}
