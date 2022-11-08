package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.vo.*;
import com.jocoos.mybeautip.domain.term.code.TermType;
import com.jocoos.mybeautip.global.vo.SearchKeyword;
import com.jocoos.mybeautip.member.Member;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.jocoos.mybeautip.domain.member.persistence.domain.QMemberActivityCount.memberActivityCount;
import static com.jocoos.mybeautip.domain.member.persistence.domain.QMemberDetail.memberDetail;
import static com.jocoos.mybeautip.domain.term.persistence.domain.QMemberTerm.memberTerm;
import static com.jocoos.mybeautip.domain.term.persistence.domain.QTerm.term;
import static com.jocoos.mybeautip.member.QMember.member;
import static com.jocoos.mybeautip.member.address.QAddress.address;
import static com.querydsl.core.group.GroupBy.groupBy;
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
        return repository.query(query -> query
                        .select(new QMemberSearchResult(member, address, memberDetail))
                        .from(member)
                        .leftJoin(memberDetail).on(member.id.eq(memberDetail.memberId))
                        .leftJoin(address).on(member.eq(address.createdBy))
                        .where(eqId(memberId)))
                .fetchOne();
    }

    @Override
    public Page<MemberBasicSearchResult> getMember(MemberSearchCondition condition) {
        JPAQuery<?> offsetSearchQuery = offsetSearch(baseSearchQuery(condition), condition.getOffset(), condition.getSize());
        JPAQuery<Long> countQuery = getCountQuery(baseSearchQuery(condition));
        return getPage(fetchBasicSearchResult(offsetSearchQuery), condition.pageable(), countQuery::fetchOne);
    }

    private JPAQuery<?> baseSearchQuery(MemberSearchCondition condition) {
        return repository.query(query -> query
                .from(member)
                .where(
                        eqStatus(condition.status()),
                        eqGrantType(condition.grantType()),
                        startAtAfter(condition.getStartAt()),
                        endAtBefore(condition.getEndAt()),
                        searchByKeyword(condition.searchKeyword())
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
                .select(new QMemberBasicSearchResult(member, memberActivityCount))
                .innerJoin(memberActivityCount).on(member.eq(memberActivityCount.member))
                .fetch();
    }

    private BooleanExpression searchByKeyword(SearchKeyword searchKeyword) {
        if (searchKeyword.isNoSearch()) {
            return null;
        }
        return Expressions.booleanOperation(
                Ops.STRING_CONTAINS_IC,
                Expressions.path(String.class, member, searchKeyword.getSearchField()),
                Expressions.constant(searchKeyword.getKeyword()));
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
