package com.jocoos.mybeautip.domain.placard.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.dto.QAdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.jocoos.mybeautip.domain.placard.persistence.domain.QPlacard.placard;
import static com.querydsl.core.types.dsl.Expressions.nullExpression;

@Repository
public class PlacardCustomRepositoryImpl implements  PlacardCustomRepository {

    private final ExtendedQuerydslJpaRepository<Placard, Long> repository;

    public PlacardCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Placard, Long> repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminPlacardResponse> getPlacards(PlacardSearchCondition condition) {
        List<AdminPlacardResponse> contents = getContents(condition);
        Long count = getCount(baseConditionQuery(condition));
        return new PageImpl<>(contents, condition.pageable(), count);
    }

    @Override
    public void toFirstOrder(Long id) {
        pushAllSortingValue(1);
        repository.update(query -> query
                .set(placard.sorting, 1)
                .where(eqId(id))
                .execute());
    }

    @Override
    public void arrangeByIndex(List<Long> ids) {
        allSortingToNull();
        IntStream.range(0, ids.size()).forEach(index -> setSortingByIndex(ids, index));
        changeIsTopFixFalseWhereSortingIsNull(); // ㅅㅜㅈㅓㅇ
    }

    @Override
    public void sortingToNull(Long placardId) {
        repository.update(query -> sortingToNull(query)
                .where(eqId(placardId))
                .execute());
    }

    private void allSortingToNull() {
        repository.update(query -> sortingToNull(query).execute());
    }

    private JPAUpdateClause sortingToNull(JPAUpdateClause query) {
        return query.set(placard.sorting, nullExpression());
    }

    private void changeIsTopFixFalseWhereSortingIsNull() {
        repository.update(query -> query
                .set(placard.isTopFix, false)
                .where(placard.sorting.isNull())
                .execute());
    }

    private void setSortingByIndex(List<Long> ids, int index) {
        repository.update(query -> query
                .set(placard.sorting, index + 1)
                .where(placard.id.eq(ids.get(index)))
                .execute());
    }

    private void pushAllSortingValue(int pushValue) {
        repository.update(query -> query
                .set(placard.sorting, placard.sorting.add(pushValue))
                .execute());
    }

    private List<AdminPlacardResponse> getContents(PlacardSearchCondition condition) {
        JPAQuery<?> query = baseConditionQuery(condition);
        return getContents(condition, query);
    }

    private List<AdminPlacardResponse> getContents(PlacardSearchCondition condition, JPAQuery<?> query) {
        return query
                .select(new QAdminPlacardResponse(placard))
                .offset(condition.offset())
                .limit(condition.limit())
                .orderBy(placard.sorting.asc().nullsLast(), placard.createdAt.desc())
                .fetch();
    }

    private JPAQuery<?> baseConditionQuery(PlacardSearchCondition condition) {
        return repository.query(query -> query
                        .from(placard)
                        .where(
                                eqStatus(condition.status()),
                                searchTitle(condition.keyword()),
                                goeStartedAt(condition.startAt()),
                                loeStartedAt(condition.endAt()),
                                eqIsTopFix(condition.IsTopFix())
                        ));
    }

    private Long getCount(JPAQuery<?> conditionQuery) {
        Long count = conditionQuery
                .select(placard.count())
                .fetchOne();
        return count == null ? 0 : count;
    }

    private BooleanExpression eqIsTopFix(Boolean isTopFix) {
        if (isTopFix == null) {
            return null;
        }
        return isTopFix ? placard.isTopFix.isTrue() : placard.isTopFix.isFalse();
    }

    private BooleanExpression eqId(Long id) {
        return placard.id.eq(id);
    }

    private BooleanExpression eqStatus(PlacardStatus status) {
        return status == null ? null : placard.status.eq(status);
    }

    private BooleanExpression searchTitle(String keyword) {
        return keyword == null ? null : placard.description.containsIgnoreCase(keyword);
    }

    private BooleanExpression goeStartedAt(ZonedDateTime startAt) {
        return startAt == null ? null : placard.startedAt.goe(startAt);
    }

    private BooleanExpression loeStartedAt(ZonedDateTime endAt) {
        return endAt == null ? null : placard.startedAt.loe(endAt);
    }
}
