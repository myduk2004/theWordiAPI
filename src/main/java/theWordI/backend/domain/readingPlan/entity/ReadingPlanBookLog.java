package theWordI.backend.domain.readingPlan.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import theWordI.backend.domain.readingPlan.dto.ReadingPlanBookCreateRequest;
import theWordI.backend.util.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
@Entity
@Table(name="bible_reading_plan_book_log", indexes={
        @Index(name = "idx_bible_reading_plan_book_log", columnList = "user_id, plan_book_id")
})
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ReadingPlanBookLog {

    @Id
    @Column(name="plan_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planLogId;

    @Column(name="plan_id")
    @NotNull
    private Long planId;


    @Column(name="plan_book_id")
//    @NotNull
    private Long planBookId;

    @Column(name="user_id")
    @NotNull
    private Long userId;

    @Column(name="book_id", columnDefinition = "smallint")
    @NotNull
    private Integer bookId;


    @Column(name="start_chapter", columnDefinition = "smallint")
    @NotNull
    private int startChapter;

    @Column(name="end_chapter", columnDefinition = "smallint")
    @NotNull
    private int endChapter;

    @Column(name="start_dt")
    private LocalDate startDt;

    @Column(name="end_dt")
    private LocalDate endDt;

    @CreatedDate
    @Column(name="reg_dt", updatable = false)
    private LocalDateTime regDt;


    public static ReadingPlanBookLog create(ReadingPlanBookCreateRequest dto, Long planId, Long planBookId)
    {
        return ReadingPlanBookLog.builder()
                .planId(planId)
                .planBookId(planBookId)
                .userId(SecurityUtil.getUserId())
                .bookId(dto.getBookId())
                .startChapter(dto.getStartChapter())
                .endChapter(dto.getEndChapter())
                .startDt(dto.getStartDt())
                .endDt(dto.getEndDt())
                .build();
    }



    public boolean isOwner()
    {
        return this.userId != null && this.userId.equals(SecurityUtil.getUserId());
    }

}
