package theWordI.backend.domain.headerVerse.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="header_verse")
@Getter
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class HeaderVerse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "header_verse_id")
    private Long headerVerseId;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotEmpty
    @Column(name = "version_id")
    private String versionId;

    @NotNull
    @Column(name = "book_id")
    private Integer bookId;

    @NotNull
    @Column(name = "chapter")
    private Integer chapter;

    @NotNull
    @Column(name = "verse")
    private Integer verse;

    @NotNull
    @Column(name = "display_order")
    private Integer displayOrder;

    @NotEmpty
    @Column(name = "active_yn")
    private String activeYn;

    @CreatedDate
    @Column(name="reg_dt", updatable = false)
    private LocalDateTime regDt;

    @LastModifiedDate
    @Column(name = "upd_dt")
    private LocalDateTime updDt;
}
