package theWordI.backend.domain.meditation.dto;

import java.time.LocalDate;

public record MeditationListResponse2(

        Long meditationId,
        String title,
        String text,
        LocalDate meditationDt
) {
    public MeditationListResponse2(Long meditationId, String title, String text, java.sql.Date meditationDt) {
        this(meditationId, title, text, meditationDt != null ? meditationDt.toLocalDate() : null);
    }
}
