package theWordI.backend.domain.headerVerse.dto;


public record HeaderVerseResponse(Long headerVerseId,
                                  Long userId,
                                  String versionId,
                                  String versionName,
                                  Integer bookId,
                                  String nameKo,
                                  Integer chapter,
                                  Integer verse,
                                  String text,
                                  Integer displayOrder) {
}