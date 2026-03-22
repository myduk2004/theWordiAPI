package theWordI.backend.domain.meditation.dto;

import java.time.LocalDate;

public record MeditationListResponse(

        Long meditationId,
        String title,
        String text,
        LocalDate meditationDt
) {
    //html태그 삭제
    public MeditationListResponse {
        text = text != null ? text.replaceAll("<[^>]*>", "") : null;
    }
}
