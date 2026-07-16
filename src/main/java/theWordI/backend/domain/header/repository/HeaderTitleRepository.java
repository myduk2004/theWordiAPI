package theWordI.backend.domain.header.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import theWordI.backend.domain.header.dto.HeaderTitleResponse;
import theWordI.backend.domain.header.entity.HeaderTitle;

import java.util.List;

@Repository
public interface HeaderTitleRepository extends JpaRepository<HeaderTitle, Long> {

    @Query("""
            select new theWordI.backend.domain.header.dto.HeaderTitleResponse(
            h.headerTitleId,
            h.userId,
            h.menuId,
            h.text,
            h.subText,
            h.source,
            h.activeYn,
            h.startDt,
            h.endDt,
            h.regDt
            )
            from HeaderTitle h
            where h.userId=:userId
            and h.activeYn='Y'
            and CURRENT_TIMESTAMP() BETWEEN startDt AND endDt
            order by h.userId, h.menuId
            """)
    List<HeaderTitleResponse> findHeaderTitles(@Param("userId") Long userId);

    @Query("""
            select new theWordI.backend.domain.header.dto.HeaderTitleResponse(
            h.headerTitleId,
            h.userId,
            h.menuId,
            h.text,
            h.subText,
            h.source,
            h.activeYn,
            h.startDt,
            h.endDt,
            h.regDt
            )
            from HeaderTitle h
            where h.userId=0
            and h.activeYn='Y'
            and CURRENT_TIMESTAMP() BETWEEN startDt AND endDt
            order by h.userId, h.menuId
            """)
    List<HeaderTitleResponse> findHeaderCommonTitles();

}
