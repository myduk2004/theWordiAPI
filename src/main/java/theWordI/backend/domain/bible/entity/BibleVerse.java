package theWordI.backend.domain.bible.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="bible_verse")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BibleVerse {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="verse_id")
    private Long verseId;

    @Column(name="version_id")
    private String versionId;

    @Column(name="book_id")
    private Integer bookId;

    @Column(name="chapter")
    private Integer chapter;

    @Column(name="verse")
    private Integer verse;

    @Column(name="text", length = 2000)
    private String text;

    @Column(name="reg_id", nullable = false, updatable = false)
    private Long regId;

    @CreationTimestamp
    @Column(name="reg_dt", nullable=false, updatable=false)
    private LocalDateTime regDt;

    @Column(name="upd_id")
    private Long updId;

    @Column(name="upd_dt")
    private LocalDateTime updDt;


    public void updateText(String text, Long userId)
    {
        this.text = text;
        this.updId = userId;
        this.updDt = LocalDateTime.now();
    }
}
