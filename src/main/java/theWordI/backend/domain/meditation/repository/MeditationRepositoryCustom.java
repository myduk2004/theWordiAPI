package theWordI.backend.domain.meditation.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import theWordI.backend.domain.meditation.dto.MeditationListResponse2;

import java.time.LocalDate;

@Repository
public interface MeditationRepositoryCustom {

    Slice<MeditationListResponse2> searchMeditations(Long userId,
                                                     String searchItem,
                                                     String keyword,
                                                     LocalDate startDt,
                                                     LocalDate endDt,
                                                     Pageable pageable);
}
