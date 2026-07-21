package theWordI.backend.domain.meditation.dto;

import java.time.LocalDate;

public interface MeditationListResponse {

    Long getMeditationId();
    String getTitle();
    String getBibleText();
    String getEtcText();
    String getEtcSource();
    LocalDate getMeditationDt();

}