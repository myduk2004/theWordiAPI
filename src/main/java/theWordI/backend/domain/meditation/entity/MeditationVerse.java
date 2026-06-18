package theWordI.backend.domain.meditation.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="meditation_verse")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class MeditationVerse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="meditation_verse_Id")
    private Long meditationVerseId;

    @Column(name="meditation_id")
    private Long meditationId;

    @Column(name="verse_id")
    private Long verseId;

    @Column(name="order_no")
    private Integer orderNo;

    @Column(name="reg_dt", updatable = false)
    @CreatedDate
    private LocalDateTime regDt;

}
