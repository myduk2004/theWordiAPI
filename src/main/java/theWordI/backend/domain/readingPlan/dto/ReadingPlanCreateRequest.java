package theWordI.backend.domain.readingPlan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import theWordI.backend.domain.readingPlan.entity.ReadingPlan;
import theWordI.backend.util.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter  //테스트를 위해, 테스트 후에 지우자
@NoArgsConstructor
public class ReadingPlanCreateRequest {

    @NotEmpty(message = "성경버전은 필수입니다.")
    private String versionId;

    private String title;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDt;

    public ReadingPlan toEntity(Integer readingCount)
    {
       return  ReadingPlan.createPlan(
               this.versionId,
               this.title,
               this.startDt,
               SecurityUtil.getUserId(),
               readingCount);
    }
}
