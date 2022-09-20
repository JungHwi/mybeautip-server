package com.jocoos.mybeautip.domain.community.persistence.repository.community;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunity.community;
import static com.jocoos.mybeautip.global.exception.ErrorCode.BAD_REQUEST;
import static io.jsonwebtoken.lang.Collections.isEmpty;

@Repository
public class CommunityCustomRepositoryImpl implements CommunityCustomRepository {

    private final ExtendedQuerydslJpaRepository<Community, Long> repository;

    public CommunityCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Community, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<Community> getCommunities(CommunitySearchCondition condition, Pageable pageable) {
        JPAQuery<Community> defaultQuery = createDefaultQuery(condition, pageable);
        addWhereConditionOptional(condition, defaultQuery);
        return defaultQuery.fetch();
    }

    private JPAQuery<Community> createDefaultQuery(CommunitySearchCondition condition, Pageable pageable) {
        return repository.query(query -> query
                .select(community)
                .from(community)
                .where(
                        inCategories(condition.getCategories()),
                        lessThanSortedAt(condition.getCursor())
                )
                .orderBy(sortCommunities(condition.isCategoryDrip())))
                .limit(pageable.getPageSize());
    }

    private void addWhereConditionOptional(CommunitySearchCondition condition, JPAQuery<Community> defaultQuery) {
        if (condition.isCategoryDrip()) {
            addWhereCondition(defaultQuery, eqEventId(condition.getEventId()), eqIsWin(condition.isFirstSearch()));
        }
    }

    private void addWhereCondition(JPAQuery<Community> defaultQuery, BooleanExpression...expressions) {
        defaultQuery.where(expressions);
    }

    private OrderSpecifier<?>[] sortCommunities(boolean isDrip) {
        if (!isDrip) {
            return new OrderSpecifier[]{community.sortedAt.desc()};
        }
        return new OrderSpecifier[]{community.isWin.desc().nullsLast(), community.sortedAt.desc()};
    }

    private BooleanExpression lessThanSortedAt(ZonedDateTime cursor) {
        return cursor == null ? null : community.sortedAt.lt(cursor);
    }

    private BooleanExpression inCategories(List<CommunityCategory> categories) {
        return isEmpty(categories) ? null : community.category.in(categories);
    }

    private BooleanExpression eqIsWin(boolean isFirstSearch) {
        return isFirstSearch ? community.isWin.isTrue().or(community.isWin.isNull()) : community.isWin.isNull();
    }

    private BooleanExpression eqEventId(Long eventId) {
        if (eventId == null || eventId < NumberUtils.LONG_ONE) {
            throw new BadRequestException(BAD_REQUEST, "event_id is required to search DRIP category.");
        }
        return community.eventId.eq(eventId);
    }

}
