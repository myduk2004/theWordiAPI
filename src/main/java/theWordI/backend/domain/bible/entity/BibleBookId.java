package theWordI.backend.domain.bible.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class BibleBookId implements Serializable {

    @Column(name="book_id")
    private Short bookId;

    @Column(name="version_id", length=20)
    private String versionId;
}
