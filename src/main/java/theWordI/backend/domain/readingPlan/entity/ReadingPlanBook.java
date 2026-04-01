package theWordI.backend.domain.readingPlan.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.EventListener;

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
    @Column(name="status", columnDefinition = "char(1) default 'W'")
    private String status = "W";

    @Column(name="start_dt")
    private LocalDateTime startDt;

    @Column(name="end_dt")
    private LocalDateTime endDt;

    @CreatedDate
    @Column(name="reg_dt", updatable = false)
    private LocalDateTime regDt;

    @LastModifiedDate
    @Column(name="upd_dt")
    private LocalDateTime updDt;

    public void update(String status,
                       LocalDateTime startDt,
                       LocalDateTime endDt)
    {
        if (StringUtils.hasText(status))
        {
            this.status = status;
        }

        if (startDt != null)
        {
            this.startDt = startDt;
        }

        if (endDt != null)
        {
            this.endDt = endDt;
        }
    }

    public boolean isOwner(Long requestUserId)
    {
        return this.userId != null && this.userId.equals(requestUserId);
    }

}
