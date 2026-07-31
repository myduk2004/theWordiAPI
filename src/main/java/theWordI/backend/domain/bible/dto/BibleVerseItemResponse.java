package theWordI.backend.domain.bible.dto;

public record BibleVerseItemResponse(Long verseId,
                                     int bookId,
                                     String bookName,
                                     int chapter,
                                     int verse,
                                     String text,
                                     String subTitle) {
}
