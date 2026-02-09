package theWordI.backend.domain.bible.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="bible_version")
@Getter
@NoArgsConstructor
public class BibleVersion {

    @Id
    @Column(name="version_id", length = 20)
    private String versionId;

    @Column(name="version_name")
    private String  versionName;

    @Column(name="language")
    private String  language;

    @Column(name="publisher")
    private String  publisher;

    @Column(name="published_dt")
    private LocalDate publishedDt;

    @Column(name="style_origin")
    private String styleOrigin;

    @Column(name="order_no")
    private Short orderNo;

    @Column(name="is_used")
    private Short isUsed;

    @Column(name="upd_dt")
    private LocalDate updDt;

}
