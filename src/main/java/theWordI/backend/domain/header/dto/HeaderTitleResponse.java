package theWordI.backend.domain.header.dto;

import java.time.LocalDateTime;

public record HeaderTitleResponse(Long headerTitleId,
                                  Long userId,
                                  Integer menuId,
                                  String text,
                                  String subText,
                                  String source,
                                  String activeYn,
                                  LocalDateTime startDt,
                                  LocalDateTime endDt,
                                  LocalDateTime regDt) {
}