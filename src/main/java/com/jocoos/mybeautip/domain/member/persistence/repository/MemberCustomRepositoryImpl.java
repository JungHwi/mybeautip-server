package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.community.dto.QMemberResponse;
import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.InfluencerStatus;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.dto.MemoResponse;
import com.jocoos.mybeautip.domain.member.dto.QMemoResponse;
import com.jocoos.mybeautip.domain.member.vo.*;
import com.jocoos.mybeautip.global.vo.Day;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.jocoos.mybeautip.domain.member.persistence.domain.QInfluencer.influencer;
import static com.jocoos.mybeautip.domain.member.persistence.domain.QMemberActivityCount.memberActivityCount;
import static com.jocoos.mybeautip.domain.member.persistence.domain.QMemberDetail.memberDetail;
import static com.jocoos.mybeautip.domain.member.persistence.domain.QMemberMemo.memberMemo;
import static com.jocoos.mybeautip.member.QMember.member;
import static com.jocoos.mybeautip.member.address.QAddress.address;
import static com.jocoos.mybeautip.member.block.BlockStatus.BLOCK;
import static com.jocoos.mybeautip.member.block.QBlock.block;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.sql.SQLExpressions.count;
import static org.springframework.data.support.PageableExecutionUtils.getPage;

@Repository
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final ExtendedQuerydslJpaRepository<Member, Long> repository;

    public MemberCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Member, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Map<MemberStatus, Long> getStatusesWithCount() {
        return repository.query(query -> query
                .select(member.status, count(member))
                .from(member)
                .groupBy(member.status)
                .transform(groupBy(member.status).as(count(member))));
    }

    @Override
    public MemberSearchResult getMemberWithDetails(Long memberId) {
        QMember memoCreatedBy = new QMember("memoCreatedBy");
        return repository.query(query -> query
                        .select(memberSearchResult(memoCreatedBy))
                        .from(member)
                        .leftJoin(memberDetail).on(memberDetail.memberId.eq(memberId))
                        .leftJoin(memberActivityCount).on(memberActivityCount.member.id.eq(memberId))
                        .leftJoin(address).on(address.createdBy.id.eq(memberId))
                        .leftJoin(memberMemo).on(memberMemo.target.id.eq(memberId))
                        .leftJoin(memoCreatedBy).on(memberMemo.createdBy.eq(memoCreatedBy))
                        .where(eqId(memberId)))
                .orderBy(memberMemo.id.asc())
                .transform(groupBy(member.id).as(memberSearchResult(memoCreatedBy)))
                .get(memberId);
    }

    @Override
    public Page<MemberBasicSearchResult> getMembers(MemberSearchCondition condition) {
        JPAQuery<?> offsetSearchQuery = offsetSearch(baseSearchQuery(condition), condition.getOffset(), condition.getSize());
        JPAQuery<Long> countQuery = getCountQuery(baseSearchQuery(condition));

        if (condition.isBlocked()) {
            onlyBlockedMember(offsetSearchQuery);
        }

        return getPage(fetchBasicSearchResult(offsetSearchQuery), condition.pageable(), countQuery::fetchOne);
    }

    @Override
    public List<Member> getMemberLastLoggedAtSameDayIn(List<Day> lastLoggedAt) {
        return repository.query(query -> query
                .select(member)
                .from(member)
                .where(
                        eqPushable(true),
                        eqVisible(true),
                        lastLoggedAtSameDayIn(lastLoggedAt)
                )
                .fetch());
    }

    private JPAQuery<?> baseSearchQuery(MemberSearchCondition condition) {
        return repository.query(query -> query
                .from(member).leftJoin(influencer).on(member.id.eq(influencer.id))
                .where(
                        eqStatus(condition.status()),
                        eqGrantType(condition.grantType()),
                        startAtAfter(condition.getStartAt()),
                        endAtBefore(condition.getEndAt()),
                        searchByKeyword(condition.searchOption()),
                        isInfluencer(condition.searchOption())
                ));
    }

    private JPAQuery<?> offsetSearch(JPAQuery<?> baseQuery, Long offset, Long size) {
        return baseQuery
                .offset(offset)
                .limit(size)
                .orderBy(member.createdAt.desc());
    }

    private JPAQuery<Long> getCountQuery(JPAQuery<?> baseQuery) {
        return baseQuery
                .select(member.count());
    }

    private List<MemberBasicSearchResult> fetchBasicSearchResult(JPAQuery<?> baseQuery) {
        return baseQuery
                .select(new QMemberBasicSearchResult(member, memberActivityCount, influencer))
                .innerJoin(memberActivityCount).on(member.eq(memberActivityCount.member))
                .fetch();
    }

    private JPAQuery<?> onlyBlockedMember(JPAQuery<?> baseQuery) {
        return baseQuery
                .join(block).on(block.memberYou.eq(member))
                .where(block.status.eq(BLOCK));
    }

    private BooleanBuilder lastLoggedAtSameDayIn(List<Day> days) {
        Predicate[] isSameDays = days.stream()
                .map(this::lastLoggedAtBetween)
                .toArray(Predicate[]::new);
        return new BooleanBuilder().andAnyOf(isSameDays);
    }

    private BooleanExpression eqVisible(Boolean visible) {
        if (visible == null) {
            return null;
        }
        return visible ? member.visible.isTrue() : member.visible.isFalse();
    }

    private BooleanExpression eqPushable(Boolean pushable) {
        if (pushable == null) {
            return null;
        }
        return pushable ? member.pushable.isTrue() : member.pushable.isFalse();
    }

    private BooleanExpression searchByKeyword(SearchOption searchOption) {
        if (searchOption == null || searchOption.isNoSearch()) {
            return null;
        }
        if (Objects.equals(searchOption.getSearchField(), "memo")) {
            return memberMemo.content.containsIgnoreCase(searchOption.getKeyword());
        }
        return Expressions.booleanOperation(
                Ops.STRING_CONTAINS_IC,
                Expressions.path(String.class, member, searchOption.getSearchField()),
                Expressions.constant(searchOption.getKeyword()));
    }

    private BooleanExpression isInfluencer(SearchOption searchOption) {
        if (searchOption == null || searchOption.getIsInfluencer() == null) {
            return null;
        }

        if (searchOption.getIsInfluencer()) {
            return influencer.status.eq(InfluencerStatus.ACTIVE);
        } else {
            return influencer.status.eq(InfluencerStatus.INACTIVE).or(influencer.status.isNull());
        }
    }

    private QMemberSearchResult memberSearchResult(QMember memoCreatedBy) {
        return new QMemberSearchResult(member, address, memberDetail, memberActivityCount, memoResponses(memoCreatedBy));
    }

    private AbstractGroupExpression<MemoResponse, List<MemoResponse>> memoResponses(QMember memoCreatedBy) {
        return list(memoResponse(memoCreatedBy).skipNulls());
    }

    private QMemoResponse memoResponse(QMember memoCreatedBy) {
        return new QMemoResponse(memberMemo, memberResponse(memoCreatedBy).skipNulls());
    }

    private QMemberResponse memberResponse(QMember member) {
        return new QMemberResponse(member.id, member.username);
    }

    private BooleanExpression lastLoggedAtBetween(Day day) {
        return member.lastLoggedAt.between(day.getStartOfDay(), day.getEndOfDay());
    }

    private static BooleanExpression eqId(Long memberId) {
        return memberId == null ? null : member.id.eq(memberId);
    }

    private BooleanExpression eqStatus(MemberStatus status) {
        return status == null ? null : member.status.eq(status);
    }

    private BooleanExpression eqGrantType(GrantType grantType) {
        return grantType == null ? null : member.link.eq(grantType.getLink());
    }

    private BooleanExpression startAtAfter(Date date) {
        return date == null ? null : member.createdAt.goe(date);
    }

    private BooleanExpression endAtBefore(Date date) {
        return date == null ? null : member.createdAt.loe(date);
    }
}
