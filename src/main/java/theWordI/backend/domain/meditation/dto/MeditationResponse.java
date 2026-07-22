package theWordI.backend.domain.meditation.dto;

import lombok.*;
import theWordI.backend.domain.bible.dto.BibleVerseResponse;
import theWordI.backend.domain.bible.dto.BibleVerseRow;
import theWordI.backend.domain.meditation.entity.Meditation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MeditationResponse {

    private Long meditationId;
    private Long userId;
    private LocalDate meditationDt;
    private String title;
    private String text;
    private String bibleText;
    private String etcText;
    private String etcSource;
    private LocalDateTime regDt;
    private List<BibleVerseResponse> bibleVerses;

    public static MeditationResponse from(
            Meditation meditation,
            List<BibleVerseResponse> bibleVerses
    ) {
        return MeditationResponse.builder()
                .meditationId(meditation.getMeditationId())
                .userId(meditation.getUserId())
                .meditationDt(meditation.getMeditationDt())
                .title(meditation.getTitle())
                .text(meditation.getText())
                .bibleText(meditation.getBibleText())
                .etcText(meditation.getEtcText())
                .etcSource(meditation.getEtcSource())
                .regDt(meditation.getRegDt())
                .bibleVerses(bibleVerses)
                .build();
    }
}