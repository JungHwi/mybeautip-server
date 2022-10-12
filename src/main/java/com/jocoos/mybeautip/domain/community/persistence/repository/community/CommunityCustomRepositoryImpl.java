package com.jocoos.mybeautip.domain.community.persistence.repository.community;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.dto.QCommunityMemberResponse;
import com.jocoos.mybeautip.domain.community.dto.QVoteResponse;
import com.jocoos.mybeautip.domain.community.dto.VoteResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.home.vo.QSummaryCommunityResult;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityCondition;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;
import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE;
import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.DELETE;
import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunity.community;
import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunityCategory.communityCategory;
import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunityFile.communityFile;
import static com.jocoos.mybeautip.domain.community.persistence.domain.vote.QCommunityVote.communityVote;
import static com.jocoos.mybeautip.domain.event.persistence.domain.QEvent.event;
import static com.jocoos.mybeautip.global.exception.ErrorCode.BAD_REQUEST;
import static com.jocoos.mybeautip.member.QMember.member;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.sql.SQLExpressions.count;
import static io.jsonwebtoken.lang.Collections.isEmpty;

@Repository
public class CommunityCustomRepositoryImpl implements CommunityCustomRepository {

    private final ExtendedQuerydslJpaRepository<Community, Long> repository;

    public CommunityCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Community, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<Community> getCommunities(CommunitySearchCondition condition, Pageable pageable) {
        JPAQuery<Community> query = baseSearchQuery(condition.getCategories(),
                                                              condition.getCursor(),
                                                              pageable.getPageSize());
        query.orderBy(sortCommunities(condition.isCategoryDrip()));
        addWhereConditionOptional(condition, query);
        return query.fetch();
    }

    @Override
    public SearchResult<Community> search(KeywordSearchCondition condition) {
        List<Community> communities = repository.query(query -> query
                .select(community)
                .from(community)
                .join(community.member, member).fetchJoin()
                .join(communityCategory).on(community.category.eq(communityCategory))
                .where(
                        searchCondition(condition.getKeyword()),
                        lessThanSortedAt(condition.getCursor()),
                        notEqStatus(DELETE),
                        ltReportCount(3)
                )
                .orderBy(sortCommunities(false))
                .limit(condition.getSize())
                .fetch());

        return new SearchResult<>(communities, countBy(condition.getKeyword()));
    }

    @Override
    public Long countBy(String keyword) {
        return repository.query(query -> query
                .select(count(community))
                .from(community)
                .join(member).on(community.member.eq(member))
                .join(communityCategory).on(community.category.eq(communityCategory))
                .where(
                        searchCondition(keyword),
                        notEqStatus(DELETE),
                        ltReportCount(3)
                )
                .fetchOne());
    }

    @Override
    public List<SummaryCommunityResult> summary(SummaryCommunityCondition condition) {
        JPAQuery<?> baseQuery = baseSummaryQuery(condition);

        switch (condition.getCategoryType()) {
            case DRIP:
                return setThumbnail(summaryWithMemberAndEventTitle(baseQuery));
            case BLIND:
                return summaryWithMember(baseQuery);
            case VOTE:
                return setVoteResponses(summaryWithMember(baseQuery));
            default:
                return setThumbnail(summaryWithMember(baseQuery));
        }
    }


    private JPAQuery<Community> baseSearchQuery(List<CommunityCategory> categories,
                                                ZonedDateTime cursor,
                                                int limitSize) {
        return repository.query(query -> query
                .select(community)
                .from(community)
                .where(
                        inCategories(categories),
                        lessThanSortedAt(cursor)
                )
                .limit(limitSize));
    }

    private JPAQuery<?> baseSummaryQuery(SummaryCommunityCondition condition) {
        return repository.query(query -> query
                .from(community)
                .where(
                        eqCategoryId(condition.getCategoryId()),
                        notEqStatus(DELETE),
                        ltReportCount(3),
                        isVotesNotEmpty(condition.getCategoryType())
                )
                .limit(condition.getSize())
                .orderBy(sortCommunities(false))
        );
    }

    private List<SummaryCommunityResult> setVoteResponses(List<SummaryCommunityResult> results) {
        List<Long> ids = getIds(results);
        Map<Long, List<VoteResponse>> votesMap = repository.query(query -> query
                        .select(new QVoteResponse(communityVote.id, communityFile.community.id, communityFile.file))
                        .from(communityFile)
                        .join(communityVote).on(communityVote.communityFile.eq(communityFile))
                        .where(communityFile.community.id.in(ids)))
                .transform(groupBy(communityFile.community.id).as(list(new QVoteResponse(communityVote.id, communityFile.community.id, communityFile.file))));

        for (SummaryCommunityResult result : results) {
            result.setVoteResponses(votesMap);
        }

        return results;
    }

    private List<SummaryCommunityResult> setThumbnail(List<SummaryCommunityResult> results) {
        List<Long> ids = getIds(results);
        Map<Long, List<CommunityFile>> fileMap = repository.query(query -> query
                .select(communityFile)
                .from(communityFile)
                .where(communityFile.community.id.in(ids))
                .transform(groupBy(communityFile.community.id).as(list(communityFile))));

        for (SummaryCommunityResult result : results) {
            result.setThumbnailUrl(fileMap);
        }

        return results;
    }

    private static List<Long> getIds(List<SummaryCommunityResult> results) {
        return results.stream().map(result -> result.getCommunity().getId()).collect(Collectors.toList());
    }

    private BooleanExpression eqCategoryId(Long categoryId) {
        return categoryId == null ? null : community.categoryId.eq(categoryId);
    }

    private void addWhereConditionOptional(CommunitySearchCondition condition, JPAQuery<Community> defaultQuery) {
        if (condition.isCategoryDrip()) {
            addWhereCondition(defaultQuery, eqEventId(condition.getEventId()), eqIsWin(condition.isFirstSearch()));
        }
    }

    private void addWhereCondition(JPAQuery<Community> defaultQuery, BooleanExpression... expressions) {
        defaultQuery.where(expressions);
    }

    private List<SummaryCommunityResult> summaryWithMember(JPAQuery<?> baseQuery) {
        return baseQuery
                .select(new QSummaryCommunityResult(community, memberResponse()))
                .join(member).on(community.member.eq(member))
                .fetch();
    }

    private List<SummaryCommunityResult> summaryWithMemberAndEventTitle(JPAQuery<?> baseQuery) {
        return baseQuery
                .select(new QSummaryCommunityResult(community, memberResponse(), event.title))
                .join(member).on(community.member.eq(member))
                .join(event).on(community.eventId.eq(event.id))
                .on(community.eventId.eq(event.id))
                .fetch();
    }

    private QCommunityMemberResponse memberResponse() {
        return new QCommunityMemberResponse(member.id, member.status, member.username, member.avatarFilename);
    }

    private OrderSpecifier<?>[] sortCommunities(boolean isWinFirst) {
        if (!isWinFirst) {
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
        return isFirstSearch ? null : community.isWin.isNull();
    }

    private BooleanExpression eqEventId(Long eventId) {
        if (eventId == null || eventId < NumberUtils.LONG_ONE) {
            throw new BadRequestException(BAD_REQUEST, "event_id is required to search DRIP category.");
        }
        return community.eventId.eq(eventId);
    }

    private BooleanExpression notEqStatus(CommunityStatus status) {
        return status == null ? null : community.status.ne(status);
    }

    private BooleanExpression ltReportCount(Integer reportCount) {
        return reportCount == null ? null : community.reportCount.lt(reportCount);
    }

    private BooleanBuilder searchCondition(String keyword) {
        return containsTitle(keyword).or(containsContents(keyword)).or(containsMemberUserName(keyword));
    }

    private BooleanBuilder containsMemberUserName(String keyword) {
        return nullSafeBuilder(() -> communityCategory.type.ne(BLIND).and(member.username.contains(keyword)));
    }

    private BooleanBuilder containsContents(String keyword) {
        return nullSafeBuilder(() ->  community.contents.contains(keyword));
    }

    private BooleanBuilder containsTitle(String keyword) {
        return nullSafeBuilder(() -> community.title.contains(keyword));
    }

    private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (NullPointerException e) {
            return new BooleanBuilder();
        }
    }

    private BooleanExpression isVotesNotEmpty(CommunityCategoryType type) {
        if (VOTE.equals(type)) {
            return community.communityVoteList.isNotEmpty();
        }
        return null;
    }
}
