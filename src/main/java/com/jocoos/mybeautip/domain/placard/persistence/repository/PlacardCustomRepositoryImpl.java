package com.jocoos.mybeautip.domain.placard.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.domain.placard.code.PlacardTabType;
import com.jocoos.mybeautip.domain.placard.dto.AdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.dto.QAdminPlacardResponse;
import com.jocoos.mybeautip.domain.placard.persistence.domain.Placard;
import com.jocoos.mybeautip.domain.placard.vo.PlacardSearchCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.jocoos.mybeautip.domain.placard.persistence.domain.QPlacard.placard;
import static com.jocoos.mybeautip.domain.placard.persistence.domain.QPlacardDetail.placardDetail;
import static com.querydsl.core.types.dsl.Expressions.nullExpression;

@Repository
public class PlacardCustomRepositoryImpl implements PlacardCustomRepository {

    private final ExtendedQuerydslJpaRepository<Placard, Long> repository;

    public PlacardCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Placard, Long> repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminPlacardResponse> getPlacardsWithCount(PlacardSearchCondition condition) {
        List<AdminPlacardResponse> contents = getContents(condition);
        Long count = getCount(baseConditionQuery(condition));
        return new PageImpl<>(contents, condition.pageable(), count);
    }

    @Override
    public List<Placard> getPlacards(PlacardSearchCondition condition) {
        return baseConditionQuery(condition)
                .select(placard)
                .join(placardDetail).on(placardDetail.placard.eq(placard))
                .orderBy(sortingAscCreatedAtDesc())
                .fetch();
    }

    @Override
    public void fixAndAddToLastOrder(Long id) {
        Integer orderCount = repository.query(query -> query
                .select(placard.count().intValue().add(1))
                .from(placard)
                .where(placard.sorting.isNotNull())
                .fetchOne());

        repository.update(query -> query
                .set(placard.sorting, orderCount)
                .set(placard.isTopFix, true)
                .where(eqId(id))
                .execute());
    }

    @Override
    public List<Long> arrangeByIndex(List<Long> ids) {
        updateAllSortingNullAndIsTopFixFalse();
        IntStream.range(0, ids.size()).forEach(index -> updateSortingByIndexAndIsTopFixTrue(ids, index));
        return getSortingOrderIds();
    }

    @Override
    public void unFixAndSortingToNull(Long id) {
        repository.update(query -> query
                .set(placard.isTopFix, false)
                .set(placard.sorting, nullExpression())
                .where(eqId(id))
                .execute());
    }

    @Override
    public long updateStatus(List<Long> ids, PlacardStatus status) {
        return repository.update(query -> query
                .set(placard.status, status)
                .where(inId(ids))
                .execute());
    }

    private BooleanExpression inId(List<Long> ids) {
        return ids == null ? null : placard.id.in(ids);
    }

    private void updateAllSortingNullAndIsTopFixFalse() {
        repository.update(query -> query
                .set(placard.isTopFix, false)
                .set(placard.sorting, nullExpression())
                .execute());
    }

    private void updateSortingByIndexAndIsTopFixTrue(List<Long> ids, int index) {
        repository.update(query -> query
                .set(placard.isTopFix, true)
                .set(placard.sorting, index + 1)
                .where(placard.id.eq(ids.get(index)))
                .execute());
    }

    private List<Long> getSortingOrderIds() {
        return repository.query(query -> query
                .select(placard.id)
                .from(placard)
                .where(placard.sorting.isNotNull())
                .orderBy(placard.sorting.asc())
                .fetch());
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
                .orderBy(sortingAscCreatedAtDesc())
                .fetch();
    }

    private JPAQuery<?> baseConditionQuery(PlacardSearchCondition condition) {
        return repository.query(query -> query
                .from(placard)
                .where(
                        eqStatus(condition.status()),
                        eqTabTye(condition.type()),
                        searchTitle(condition.keyword()),
                        ltStartedAt(condition.between()),
                        gtEndedAt(condition.between()),
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

    private OrderSpecifier<?>[] sortingAscCreatedAtDesc() {
        return new OrderSpecifier[]{placard.sorting.asc().nullsLast(), placard.createdAt.desc()};
    }

    private BooleanExpression gtEndedAt(ZonedDateTime endedAt) {
        return endedAt == null ? null : placard.endedAt.gt(endedAt);
    }

    private BooleanExpression ltStartedAt(ZonedDateTime startedAt) {
        return startedAt == null ? null : placard.startedAt.lt(startedAt);
    }

    private BooleanExpression eqTabTye(PlacardTabType tabType) {
        return tabType == null ? null : placardDetail.tabType.eq(tabType);
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
