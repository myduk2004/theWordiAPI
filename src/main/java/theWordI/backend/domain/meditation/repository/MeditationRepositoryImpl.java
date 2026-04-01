package theWordI.backend.domain.meditation.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import theWordI.backend.domain.meditation.dto.MeditationListResponse2;
import theWordI.backend.domain.meditation.entity.QMeditation;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class MeditationRepositoryImpl implements MeditationRepositoryCustom {

    private final JPAQueryFactory query;
    private final QMeditation m = QMeditation.meditation;

    @Override
    public Slice<MeditationListResponse2> searchMeditations(Long userId,
                                                            String searchItem,
                                                            String keyword,
                                                            LocalDate startDt,
                                                            LocalDate endDt,
                                                            Pageable pageable) {


        StringExpression textSubstring = new CaseBuilder()
                .when(m.text.length().gt(100))
                .then(m.text.substring(0, 100))
                .otherwise(m.text);


        List<MeditationListResponse2> content = query
                .select(
                        Projections.constructor(MeditationListResponse2.class,
                                m.meditationId, m.title, textSubstring, m.meditationDt
                        )
                )
                .from(m)
                .where(
                        m.userId.eq(userId),
                        dateBetween(startDt, endDt),
                        searchColumnEq(searchItem, keyword)
                )
                .orderBy(m.meditationId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();


        boolean hasNext = content.size() > pageable.getPageSize();
        List<MeditationListResponse2> sliced = (hasNext) ?
                                content.subList(0, pageable.getPageSize())//넘친 1개 제거
                                : content;

        return new SliceImpl<>(sliced, pageable, hasNext);
    }

    private BooleanExpression dateBetween(LocalDate startDt, LocalDate endDt) {

        if (startDt == null && endDt == null) return null;
        if (startDt == null) return m.meditationDt.loe(endDt); // <=
        if (endDt == null) return m.meditationDt.goe(startDt); // >=
        return m.meditationDt.between(startDt, endDt);
    }

    private BooleanExpression searchColumnEq(String searchItem, String keyword) {
        if (searchItem == null || keyword == null) return null;

        return "title".equals(searchItem)? m.title.contains(keyword) :
                m.text.contains(keyword);
    }
}
