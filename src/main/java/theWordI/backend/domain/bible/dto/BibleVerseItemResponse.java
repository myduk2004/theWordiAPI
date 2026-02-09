package theWordI.backend.domain.bible.dto;

public record BibleVerseItemResponse(Long verseId,
                                     int bookId,
                                     int chapter,
                                     int verse,
                                     String text) {
}
