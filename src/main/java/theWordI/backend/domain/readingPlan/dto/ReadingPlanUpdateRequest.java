package theWordI.backend.domain.readingPlan.dto;



import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter //test를 위해 임시로
@NoArgsConstructor
public class ReadingPlanUpdateRequest { 
  
    private String versionId;

    @NotEmpty(message = "제목은 필수입니다.")
    private String title;

    private LocalDate startDt;

}
