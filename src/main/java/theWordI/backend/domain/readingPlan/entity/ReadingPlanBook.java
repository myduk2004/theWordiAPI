package theWordI.backend.domain.readingPlan.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;
import theWordI.backend.domain.readingPlan.dto.ReadingPlanBookCreateRequest;
import theWordI.backend.util.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="bible_reading_plan_book")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ReadingPlanBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="plan_book_id")
    private Long planBookId;

    @NotNull
    @Column(name="plan_id")
    private Long planId;

    @NotNull
    @Column(name="user_id")
    private Long userId;

    @NotNull
    @Column(name="book_id", columnDefinition = "smallint")
    private Integer bookId;

    @NotNull
    @Column(name="total_chapters", columnDefinition = "smallint")
    private Integer totalChapters;


    @Builder.Default
    @Column(name="read_chapters_cnt", columnDefinition = "smallint default 0")
    private Integer readChaptersCnt = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private PlanStatus status = PlanStatus.WAITING;

    @Column(name="start_dt")
    private LocalDate startDt;

    @Column(name="end_dt")
    private LocalDate endDt;

    @CreatedDate
    @Column(name="reg_dt", updatable = false)
    private LocalDateTime regDt;

    @LastModifiedDate
    @Column(name="upd_dt")
    private LocalDateTime updDt;

    public static ReadingPlanBook create(Long planId, ReadingPlanBookCreateRequest dto, int readCnt, PlanStatus status)
    {
        return ReadingPlanBook.builder()
                .planId(planId)
                .userId(SecurityUtil.getUserId())
                .bookId(dto.getBookId())
                .totalChapters(BookInfo.getTotalChapter(dto.getBookId()))
                .readChaptersCnt(readCnt)
                .status(status)
                .startDt(dto.getStartDt())
                .endDt(dto.getEndDt())
                .build();
    }

    public void update(PlanStatus status,
                       Integer readChaptersCnt,
                       LocalDate startDt)
    {


        if (status != null)
        {
            this.status = status;
        }

        if (readChaptersCnt != null &&  readChaptersCnt > 0)
        {
            this.readChaptersCnt = readChaptersCnt;
        }

        if (startDt != null)
        {
            this.startDt = startDt;
        }

        if (this.status ==  PlanStatus.COMPLETED)
        {
            this.endDt = LocalDate.now();
        }
    }

    public void updatePreStatus(Integer readChaptersCnt)
    {
        this.status = PlanStatus.PROCEEDING;

        if (readChaptersCnt != null &&  readChaptersCnt > 0)
        {
            this.readChaptersCnt = readChaptersCnt;
        }
        this.endDt = null;
    }

    public boolean isOwner()
    {
        return this.userId != null && this.userId.equals(SecurityUtil.getUserId());
    }

}
