package theWordI.backend.domain.bible.dto;

import java.util.List;

public record BibleVerseResponse(
        BibleVersionResponse bibleVersion,
        List<BibleVerseItemResponse> verses
) {
}
