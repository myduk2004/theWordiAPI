package theWordI.backend.domain.bible.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BibleBookResponse {
    private String versionId;
    private Short bookId;
    private String nameKo;
    private String testament;
    private Short chapterCount;
}
