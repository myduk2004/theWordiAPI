package theWordI.backend.domain.meditation.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import theWordI.backend.domain.meditation.dto.MeditationListResponse;

import java.time.LocalDate;

@Repository
public interface MeditationRepositoryCustom {

    Slice<MeditationListResponse> searchMeditations(Long userId,
                                                    String searchItem,
                                                    String keyword,
                                                    LocalDate startDt,
                                                    LocalDate endDt,
                                                    Pageable pageable);
}
