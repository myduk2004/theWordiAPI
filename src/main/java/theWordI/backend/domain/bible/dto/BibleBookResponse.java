package theWordI.backend.domain.bible.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BibleBookResponse {
    private String versionId;
    private int bookId;
    private String nameKo;
    private String testament;
    private int chapterCount;

    public static BibleBookResponse from(String versionId, int bookId, String nameKo, String testament, int chapterCount)
    {
        return BibleBookResponse.builder()
                .versionId(versionId)
                .bookId(bookId)
                .nameKo(nameKo)
                .testament(testament)
                .chapterCount(chapterCount)
                .build();
    }
}
