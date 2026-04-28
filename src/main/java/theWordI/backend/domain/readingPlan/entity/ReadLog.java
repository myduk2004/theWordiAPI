package theWordI.backend.domain.readingPlan.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import theWordI.backend.util.SecurityUtil;


@Entity
@Table(name="bible_read_log")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ReadLog {
    @Id
    @Column(name = "read_log_id ")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long readLogId;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "plan_id")
    Long planId;

    @Column(name = "plan_book_id")
    Long  planBookId;


    @Column(name = "plan_log_id")
    Long  planLogId;

    @Column(name = "book_id")
    Integer bookId;

    @Column(name = "chapter_num")
    Integer chapterNum;


    public static ReadLog create(Long planId, Long  planBookId, Long planLogId, Integer bookId, Integer chapterNum)
    {
        return ReadLog.builder()
                .userId(SecurityUtil.getUserId())
                .planId(planId)
                .planBookId(planBookId)
                .planLogId(planLogId)
                .bookId(bookId)
                .chapterNum(chapterNum)
                .build();
    }
}
