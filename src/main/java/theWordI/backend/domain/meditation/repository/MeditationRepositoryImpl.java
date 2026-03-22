package theWordI.backend.domain.meditation.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import theWordI.backend.domain.meditation.dto.MeditationListResponse;
import theWordI.backend.domain.meditation.entity.QMeditation;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class MeditationRepositoryImpl implements MeditationRepositoryCustom {

    private final JPAQueryFactory query;
    private final QMeditation m = QMeditation.meditation;

    @Override
    public Slice<MeditationListResponse> searchMeditations(Long userId,
                                                           String searchItem,
                                                           String keyword,
                                                           LocalDate startDt,
                                                           LocalDate endDt,
                                                           Pageable pageable) {


        StringExpression textSubstring = new CaseBuilder()
                .when(m.text.length().gt(100))
                .then(m.text.substring(0, 100))
                .otherwise(m.text);

        List<MeditationListResponse> content = query
                .select(
                        Projections.constructor(MeditationListResponse.class,
                                m.meditationId, m.title, textSubstring, m.meditationDt
                        )
                )
                .from(m)
                .where(
                        m.userId.eq(userId),
                        dateBetween(startDt, endDt),
                        searchColumnEq(searchItem, keyword)
                )
                .orderBy(m.meditationId.desc(), m.regDt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) content.remove(pageable.getPageSize()); //넘친 1개 제거

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression dateBetween(LocalDate startDt, LocalDate endDt) {

        if (startDt == null || endDt == null) return null;
        return m.meditationDt.between(startDt, endDt);
    }

    private BooleanExpression searchColumnEq(String searchItem, String keyword) {
        if (searchItem == null || keyword == null) return null;

        return "title".equals(searchItem)? m.title.contains(keyword) :
                m.text.contains(keyword);
    }
}
