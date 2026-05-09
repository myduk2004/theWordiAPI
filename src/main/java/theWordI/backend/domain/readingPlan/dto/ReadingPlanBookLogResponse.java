package theWordI.backend.domain.readingPlan.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import theWordI.backend.domain.readingPlan.entity.ReadingPlanBookLog;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReadingPlanBookLogResponse {
    private Long planLogId;
    private Long planId;
    private Long planBookId;
    private Integer bookId;
    private int startChapter;
    private int endChapter;
    private LocalDate startDt;
    private LocalDate endDt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime regDt;

    public static ReadingPlanBookLogResponse from(ReadingPlanBookLog entity)
    {
        return ReadingPlanBookLogResponse.builder()
                .planLogId(entity.getPlanLogId())
                .planId(entity.getPlanId())
                .planBookId(entity.getPlanBookId())
                .bookId(entity.getBookId())
                .startChapter(entity.getStartChapter())
                .endChapter(entity.getEndChapter())
                .startDt(entity.getStartDt())
                .endDt(entity.getEndDt())
                .regDt(entity.getRegDt())
                .build();
    }
}
