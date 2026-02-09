package theWordI.backend.domain.bible.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="bible_book")
@NoArgsConstructor
@Getter
public class BibleBook {

    @EmbeddedId
    private BibleBookId id;

    @Column(name="book_code")
    private String bookCode;

    @Column(name="name_ko")
    private String nameKo;

    @Column(name="name_en")
    private String nameEn ;

    @Column(name="title")
    private String title;

    @Column(name="testament")
    private String testament;

    @Column(name="book_abbr")
    private String bookAbbr;

    @Column(name="chapter_count")
    private Short chapterCount;

    @Column(name="status")
    private Byte status;

    @Column(name="upd_dt")
    private LocalDateTime updDt;


}
