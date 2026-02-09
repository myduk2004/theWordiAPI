package theWordI.backend.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import theWordI.backend.domain.jwt.repository.RefreshRepository;

import java.time.LocalDateTime;

@Component
public class ScheduleConfig {

    private final RefreshRepository refreshRepository;

    public ScheduleConfig(RefreshRepository refreshRepository) {
        this.refreshRepository = refreshRepository;
    }

    // Refresh 토큰 저장소 8일 지난 토큰 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshEntityTtlSchedule() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(8);
        refreshRepository.deleteByCreatedDtBefore(cutoff);
    }

}