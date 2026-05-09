package theWordI.backend.domain.readingPlan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import theWordI.backend.domain.readingPlan.entity.PlanStatus;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReadingPlanBookResponse {

    private Long planId;
    private Long planBookId;
    private Integer bookId;
    private Integer readChaptersCnt;
    private PlanStatus status;
    private String nameKo;
    private Short chapterCount;
    private LocalDate startDt;
    private LocalDate endDt;
    private Integer maxChapterNum;
}
