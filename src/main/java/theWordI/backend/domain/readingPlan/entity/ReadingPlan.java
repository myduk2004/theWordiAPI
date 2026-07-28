package theWordI.backend.domain.readingPlan.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;
import theWordI.backend.util.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="bible_reading_plan")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ReadingPlan {
    private static final int TOTAL_BIBLE_BOOKS = 66; // 성경 총 수
    
    @Id
    @Column(name="plan_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;


    @Column(name="user_id")
    @NotNull
    private Long userId;


    @Column(name="version_id")
    @NotEmpty
    private String versionId;


    @Column(name="read_count")
    private int readCount;


    @Column(name="book_count")
    private int bookCount;

    @Column(name="title")
    private String title;

    @Column(name="start_dt")
    @NotNull
    private LocalDate startDt;

    @Column(name="end_dt")
    private LocalDateTime endDt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name="status", length = 20)
    private PlanStatus status = PlanStatus.WAITING;

    @CreatedDate
    @Column(name="reg_dt", updatable = false)
    private LocalDateTime regDt;

    @LastModifiedDate
    @Column(name="upd_dt")
    private LocalDateTime updDt;

    public static ReadingPlan createPlan(String versionId, String title, LocalDate startDt, Long userId, int readCount, int bookCount)
    {
        return ReadingPlan.builder()
                .userId(userId)
                .versionId(versionId)
                .title(title)
                .readCount(readCount)
                .bookCount(bookCount)
                .startDt(startDt)
                .build();
    }

    public void clearEndDt()
    {
        this.endDt = null;
    }

    public void update(String title)
    {
        if (StringUtils.hasText(title))
        {
            this.title = title;
        }
    }


    public void updateProceedingPlan(PlanStatus bookStatus)
    {
        if (bookStatus == PlanStatus.COMPLETED)
        {
            this.bookCount = this.bookCount + 1;
        }

        this.status = PlanStatus.PROCEEDING;
    }

    public void updatePlan()
    {
        int newBookCount = this.bookCount + 1;
        this.bookCount = newBookCount;

        if (TOTAL_BIBLE_BOOKS <= newBookCount)
        {
            updateCompletePlan(newBookCount);
        }
    }

    private void updateCompletePlan(int newBookCount)
    {
        //this.bookCount = newBookCount;
        this.status = PlanStatus.COMPLETED;
        this.endDt = LocalDateTime.now();
    }

    public void updatePreStatus()
    {
        this.status = PlanStatus.PROCEEDING;
        this.bookCount = (this.bookCount > 0)?this.bookCount-1 : 0;
        this.endDt = null;
    }


    public boolean isOwner()
    {
        return this.userId != null && this.userId.equals((SecurityUtil.getUserId()));
    }
}
