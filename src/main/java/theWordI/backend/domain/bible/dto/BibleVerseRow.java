package theWordI.backend.domain.bible.dto;

import lombok.Getter;

@Getter
public class BibleVerseRow
{
    private final Long verseId;
    private final String versionId;
    private final String versionName;
    private final int bookId;
    private final int chapter;
    private final int verse;
    private final String text;

    public BibleVerseRow(
            Long verseId,
            String versionId,
            String versionName,
            int bookId,
            int chapter,
            int verse,
            String text
    ) {
        this.verseId = verseId;
        this.versionId = versionId;
        this.versionName = versionName;
        this.bookId = bookId;
        this.chapter = chapter;
        this.verse = verse;
        this.text = text;
    }
}
