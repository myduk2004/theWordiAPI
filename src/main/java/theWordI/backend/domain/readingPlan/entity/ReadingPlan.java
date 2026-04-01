package theWordI.backend.domain.readingPlan.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Table(name="bible_reading_plan")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ReadingPlan {

    @Id
    @Column(name="plan_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(name="user_id")
    @NotNull
    private Long userId;

    @Column(name="reading_count", columnDefinition = "smallint")
    @NotNull
    private Integer readingCount;

    @Column(name="title")
    private String title;

    @Column(name="start_dt")
    private LocalDateTime startDt;

    @Column(name="end_dt")
    private LocalDateTime endDt;

    @Builder.Default
    @Column(name="status", columnDefinition = "char(1) default 'W'")
    private String status = "W";

    @CreatedDate
    @Column(name="reg_dt", updatable = false)
    private LocalDateTime regDt;

    @LastModifiedDate
    @Column(name="upd_dt")
    private LocalDateTime updDt;


    public void update(String title, LocalDateTime startDt, LocalDateTime endDt)
    {
        if (StringUtils.hasText(title))
        {
            this.title = title;
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
        return this.userId != null && this.userId.equals((requestUserId));
    }
}
