package theWordI.backend.domain.meditation.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="meditation")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Meditation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="meditation_id")
    private Long meditationId;

    @Column(name="user_id")
    private Long userId;

    @Column(name="meditation_dt")
    private LocalDate meditationDt;

    @Column(name="title", length = 255)
    private String title;

    @Column(name="text", columnDefinition = "LONGTEXT")
    private String text;

    @CreatedDate
    @Column(name="reg_dt", updatable = false)
    private LocalDateTime regDt;

    @LastModifiedDate //수정 시 자동 저장
    @Column(name="upd_dt")
    private LocalDateTime updDt;



    public void update(LocalDate meditationDt,
                       String title,
                       String text)
    {
        if (meditationDt != null)
        {
            this.meditationDt = meditationDt;
        }

        if (StringUtils.hasText(title))
        {
            this.title = title;
        }

        if (text != null)
        {
            this.text = text;
        }
    }


    //권한체크 로직을 엔티티 내부로 정의
    public Boolean isOwner(Long requestUserId)
    {
        return this.userId != null && this.userId.equals(requestUserId);
    }
}
