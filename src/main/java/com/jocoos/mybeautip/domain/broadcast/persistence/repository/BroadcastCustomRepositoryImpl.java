package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.Broadcast;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchCondition;
import com.jocoos.mybeautip.domain.broadcast.vo.BroadcastSearchResult;
import com.jocoos.mybeautip.domain.broadcast.vo.QBroadcastSearchResult;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcast.broadcast;
import static com.jocoos.mybeautip.domain.broadcast.persistence.domain.QBroadcastCategory.broadcastCategory;
import static com.jocoos.mybeautip.member.QMember.member;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;
import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
public class BroadcastCustomRepositoryImpl implements BroadcastCustomRepository {

    private static final String TUPLE_WITH_THREE_PARAMS = "({0}, {1}, {2})";
    private static final StringTemplate BROADCAST_CURSOR_COMPARISON_TUPLE = stringTemplate(
            TUPLE_WITH_THREE_PARAMS,
            broadcast.startedAt,
            broadcast.createdAt,
            broadcast.id);
    private final ExtendedQuerydslJpaRepository<Broadcast, Long> repository;

    public BroadcastCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Broadcast, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<BroadcastSearchResult> getList(BroadcastSearchCondition condition) {
        return repository.query(query -> query
                .select(new QBroadcastSearchResult(broadcast, broadcastCategory, member))
                .from(broadcast)
                .join(member).on(broadcast.memberId.eq(member.id))
                .join(broadcastCategory).on(broadcast.category.eq(broadcastCategory))
                .where(
                        startAt(condition.startOfDay(), condition.endOfDay()),
                        createdAtAfter(condition.startAt()),
                        createdAtBefore(condition.endAt()),
                        searchByKeyword(condition.searchOption()),
                        inStatus(condition.statuses()),
                        cursor(condition.cursor())
                )
                .orderBy(
                        broadcast.sortedStatus.asc(),
                        broadcast.startedAt.desc(),
                        broadcast.createdAt.desc(),
                        broadcast.id.desc()
                )
                .offset(condition.offset())
                .limit(condition.size())
                .fetch());
    }

    // 커서 기반 페이지네이션과 유니크하지 않은 컬럼 정렬을 동시에 하기 위해 튜플 비교를 한다
    private BooleanExpression cursor(Long cursor) {
        if (cursor == null) {
            return null;
        }

        Tuple cursorValues = repository.query(query -> query
                .select(broadcast.sortedStatus, broadcast.startedAt, broadcast.createdAt)
                .from(broadcast)
                .where(eqId(cursor))
                .fetchOne());

        StringTemplate cursorValuesTuple = stringTemplate(
                TUPLE_WITH_THREE_PARAMS,
                cursorValues.get(broadcast.startedAt),
                cursorValues.get(broadcast.createdAt),
                cursor);

        BooleanExpression isBroadcastTupleLessThanCursorValuesTuple =
                BROADCAST_CURSOR_COMPARISON_TUPLE.lt(cursorValuesTuple);

        Integer sortedStatus = cursorValues.get(broadcast.sortedStatus);
        return broadcast.sortedStatus.gt(sortedStatus)
                .or(broadcast.sortedStatus.eq(sortedStatus).and(isBroadcastTupleLessThanCursorValuesTuple));
    }

    private BooleanExpression searchByKeyword(SearchOption searchOption) {
        if (searchOption == null || searchOption.isNoKeywordSearch()) {
            return null;
        }
        String keyword = searchOption.getKeyword();
        if (Objects.equals(searchOption.getSearchField(), "username")) {
            return member.username.containsIgnoreCase(keyword);
        }
        return broadcast.title.containsIgnoreCase(keyword);
    }

    private BooleanExpression startAt(ZonedDateTime from, ZonedDateTime to) {
        return from == null || to == null ? null : broadcast.startedAt.between(from, to);
    }

    private BooleanExpression createdAtAfter(ZonedDateTime dateTime) {
        return dateTime == null ? null : broadcast.createdAt.goe(dateTime);
    }

    private BooleanExpression createdAtBefore(ZonedDateTime dateTime) {
        return dateTime == null ? null : broadcast.createdAt.loe(dateTime);
    }

    private BooleanExpression inStatus(List<BroadcastStatus> statuses) {
        return isEmpty(statuses) ? null : broadcast.status.in(statuses);
    }

    private BooleanExpression eqId(Long id) {
        return id == null ? null : broadcast.id.eq(id);
    }
}
