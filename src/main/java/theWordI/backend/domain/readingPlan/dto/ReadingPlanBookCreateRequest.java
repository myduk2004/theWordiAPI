package theWordI.backend.domain.readingPlan.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter //테스트를 위해 잠시
@NoArgsConstructor //테스트용
public class ReadingPlanBookCreateRequest {

    private Long planBookId;

    private Integer bookId;

    private int startChapter;

    private int endChapter;

    private LocalDate startDt;

    private LocalDate endDt;

}
