package com.jocoos.mybeautip.domain.community.persistence.repository.community;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityFile;
import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityVoteRepository;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.home.vo.QSummaryCommunityResult;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityCondition;
import com.jocoos.mybeautip.domain.home.vo.SummaryCommunityResult;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.member.block.BlockStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;
import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.VOTE;
import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.NORMAL;
import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunity.community;
import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunityCategory.communityCategory;
import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunityFile.communityFile;
import static com.jocoos.mybeautip.domain.community.persistence.domain.vote.QCommunityVote.communityVote;
import static com.jocoos.mybeautip.domain.community.vo.CommunityOrder.order;
import static com.jocoos.mybeautip.domain.community.vo.CommunityOrder.sortedAt;
import static com.jocoos.mybeautip.domain.event.persistence.domain.QEvent.event;
import static com.jocoos.mybeautip.global.exception.ErrorCode.BAD_REQUEST;
import static com.jocoos.mybeautip.member.QMember.member;
import static com.jocoos.mybeautip.member.block.QBlock.block;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.sql.SQLExpressions.count;
import static io.jsonwebtoken.lang.Collections.isEmpty;

@Repository
public class CommunityCustomRepositoryImpl implements CommunityCustomRepository {

    private final ExtendedQuerydslJpaRepository<Community, Long> repository;
    private final CommunityVoteRepository communityVoteRepository;

    public CommunityCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Community, Long> repository,
                                         @Lazy CommunityVoteRepository communityVoteRepository) {
        this.repository = repository;
        this.communityVoteRepository = communityVoteRepository;
    }

    @Override
    public List<Community> getCommunities(CommunitySearchCondition condition, Pageable pageable) {
        JPAQuery<Community> query = baseSearchQuery(condition.categories(),
                                                              condition.cursor(),
                                                              pageable.getPageSize());
        query.orderBy(order(condition.isCategoryDrip()));
        addWhereConditionOptional(condition, query);
        dynamicQueryForLogin(query, condition.memberId());
        List<Community> communityList = query.fetch();

        List<Long> ids = communityList.stream()
                .map(Community::getId)
                .toList();

        List<CommunityVote> communityVoteList = communityVoteRepository.findByCommunityIdIn(ids);
        Map<Long, List<CommunityVote>> communityVoteMap = communityVoteList.stream()
                .collect(Collectors.groupingBy(communityVote -> communityVote.getCommunity().getId()));

        for (Community community : communityList) {
            List<CommunityVote> votes = communityVoteMap.getOrDefault(community.getId(), new ArrayList<>());
                community.setCommunityVoteList(votes);
        }

        return communityList;
    }

    @Override
    public Page<AdminCommunityResponse> getCommunitiesAllStatus(CommunitySearchCondition condition) {
        List<AdminCommunityResponse> responses = repository.query(query -> query
                        .select(new QAdminCommunityResponse(community, new QCommunityCategoryResponse(communityCategory.id, communityCategory.type, communityCategory.title), new QCommunityMemberResponse(member.id, member.status, member.username, member.avatarFilename), event.title))
                        .from(community)
                        .join(member).on(community.member.eq(member))
                        .join(communityCategory).on(community.category.eq(communityCategory))
                        .leftJoin(event).on(community.eventId.eq(event.id))
                        .where(
                                inCategories(condition.categories()),
                                searchByKeyword(condition.searchOption()),
                                createdAtAfter(condition.getStartAt()),
                                createdAtBefore(condition.getEndAt()),
                                isReported(condition.isReported())
                        )
                        .orderBy(getOrders(condition.getSort()))
                        .offset(condition.getOffset())
                        .limit(condition.getPageSize()))
                .fetch();

        List<Long> ids = responses.stream().map(AdminCommunityResponse::getId).toList();


        Map<Long, List<CommunityFile>> fileMap = repository.query(query -> query
                .select(communityFile)
                .from(communityFile)
                .join(community).on(communityFile.community.eq(community))
                .where(
                        communityFile.community.id.in(ids))
        ).transform(groupBy(communityFile.community.id).as(list(communityFile)));


        Map<Long, List<VoteResponse>> votesMap = getVotesMap(ids);

        for (AdminCommunityResponse response : responses) {
            response.setFileUrls(fileMap);
            response.setVotes(votesMap);
        }

        JPAQuery<Long> countQuery = repository.query(query -> query
                .select(count(community))
                .from(community)
                .join(member).on(community.member.eq(member))
                .where(
                        eqStatus(condition.status()),
                        inCategories(condition.categories()),
                        searchByKeyword(condition.searchOption()),
                        createdAtAfter(condition.getStartAt()),
                        createdAtBefore(condition.getEndAt()),
                        isReported(condition.isReported())
                ));
        dynamicQueryForEvent(condition.eventId(), countQuery);


        return PageableExecutionUtils.getPage(responses, condition.pageable(), countQuery::fetchOne);
    }

    private void dynamicQueryForEvent(Long eventId, JPAQuery<?> baseQuery) {
        if (eventId != null) {
            baseQuery
                    .leftJoin(event).on(community.eventId.eq(event.id))
                    .where(community.eventId.eq(eventId));
        }
    }

    private BooleanExpression isReported(Boolean isReported) {
        if (isReported == null) {
            return null;
        }
        return isReported ? community.reportCount.goe(3) : community.reportCount.eq(0);
    }

    private OrderSpecifier<?>[] getOrders(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>(getDefaultTopOrders());

        OrderSpecifier<?> requestOrder = sort.stream()
                .findFirst()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    SimplePath<Community> path = Expressions.path(Community.class, community, order.getProperty());
                    return new OrderSpecifier(direction, path);
                })
                .orElse(community.sortedAt.desc());

        orderSpecifiers.add(requestOrder);

        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

    private static List<OrderSpecifier<Boolean>> getDefaultTopOrders() {
        return Arrays.asList(community.isTopFix.desc(), community.isWin.desc());
    }

    private BooleanExpression searchByKeyword(SearchOption searchOption) {
        if (searchOption.isNoSearch()) {
            return null;
        }
        if (Objects.equals(searchOption.getSearchField(), "username")) {
            return community.category.type.ne(BLIND).and(member.username.contains(searchOption.getKeyword()));
        }
        return Expressions.booleanOperation(
                Ops.STRING_CONTAINS_IC,
                Expressions.path(String.class, community, searchOption.getSearchField()),
                Expressions.constant(searchOption.getKeyword()));
    }

    @Override
    public SearchResult<Community> search(KeywordSearchCondition condition) {
        JPAQuery<Community> baseQuery = repository.query(query -> query
                .select(community)
                .from(community)
                .join(community.member, member).fetchJoin()
                .join(communityCategory).on(community.category.eq(communityCategory))
                .where(
                        searchCondition(condition.getKeyword()),
                        lessThanSortedAt(condition.getCursor()),
                        eqStatus(NORMAL),
                        ltReportCount(3)
                )
                .orderBy(sortedAt())
                .limit(condition.getSize()));
        dynamicQueryForLogin(baseQuery, condition.getMemberId());
        List<Community> communities = baseQuery.fetch();

        return new SearchResult<>(communities, countBy(condition.getKeyword(), condition.getMemberId()));
    }

    @Override
    public Long countBy(String keyword, Long memberId) {
        JPAQuery<?> baseQuery = repository.query(query -> query
                .from(community)
                .join(member).on(community.member.eq(member))
                .join(communityCategory).on(community.category.eq(communityCategory))
                .where(
                        searchCondition(keyword),
                        eqStatus(NORMAL),
                        ltReportCount(3)
                ));

        dynamicQueryForLogin(baseQuery, memberId);
        return baseQuery.select(community.count()).fetchOne();
    }

    @Override
    public List<SummaryCommunityResult> summary(SummaryCommunityCondition condition) {
        JPAQuery<?> baseQuery = baseSummaryQuery(condition);
        dynamicQueryForLogin(baseQuery, condition.memberId());

        return switch (condition.categoryType()) {
            case DRIP -> setThumbnail(summaryWithMemberAndEventTitle(baseQuery));
            case BLIND -> summaryWithMember(baseQuery);
            case VOTE -> setVoteResponses(summaryWithMember(baseQuery));
            default -> setThumbnail(summaryWithMember(baseQuery));
        };
    }


    private JPAQuery<Community> baseSearchQuery(List<CommunityCategory> categories,
                                                ZonedDateTime cursor,
                                                int limitSize) {
        return repository.query(query -> query
                .select(community)
                .from(community)
                .join(community.member, member).fetchJoin()
                .join(community.category, communityCategory).fetchJoin()
                .leftJoin(community.communityFileList, communityFile).fetchJoin()
                .where(
                        inCategories(categories),
                        lessThanSortedAt(cursor),
                        eqStatus(NORMAL)
                )
                .limit(limitSize));
    }

    private JPAQuery<?> baseSummaryQuery(SummaryCommunityCondition condition) {
        return repository.query(query -> query
                .from(community)
                .where(
                        eqCategoryId(condition.categoryId()),
                        eqStatus(NORMAL),
                        ltReportCount(3),
                        isVotesNotEmpty(condition.categoryType())
                )
                .limit(condition.size())
                .orderBy(sortedAt())
        );
    }

    private List<SummaryCommunityResult> setVoteResponses(List<SummaryCommunityResult> results) {
        List<Long> ids = getIds(results);
        Map<Long, List<VoteResponse>> votesMap = getVotesMap(ids);

        for (SummaryCommunityResult result : results) {
            result.setVoteResponses(votesMap);
        }

        return results;
    }

    private Map<Long, List<VoteResponse>> getVotesMap(List<Long> ids) {
        return repository.query(query -> query
                        .select(new QVoteResponse(communityVote.id, communityFile.community.id, communityFile.file))
                        .from(communityFile)
                        .join(communityVote).on(communityVote.communityFile.eq(communityFile))
                        .where(communityFile.community.id.in(ids)))
                .transform(groupBy(communityFile.community.id).as(list(new QVoteResponse(communityVote.id, communityFile.community.id, communityFile.file))));
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
            addWhereCondition(defaultQuery, eqEventId(condition.eventId()), eqIsWin(condition.isFirstSearch()));
        }
    }

    private void addWhereCondition(JPAQuery<Community> defaultQuery, BooleanExpression... expressions) {
        defaultQuery.where(expressions);
    }

    private void dynamicQueryForLogin(JPAQuery<?> query, Long memberId) {
        if (memberId != null) {
            dynamicQueryForBlock(query, memberId);
        }
    }

    private void dynamicQueryForBlock(JPAQuery<?> query, Long memberId) {
        query
                .leftJoin(block).on(community.member.id.eq(block.memberYou.id).and(block.me.eq(memberId)).and(block.status.eq(BlockStatus.BLOCK)))
                .where(community.category.type.eq(BLIND).or(block.memberYou.id.isNull().and(community.category.type.ne(BLIND))));
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

    private BooleanExpression eqStatus(CommunityStatus status) {
        return status == null ? null : community.status.eq(status);
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
        return nullSafeBuilder(() -> containsMemberUsernameExpression(keyword));
    }

    private static BooleanExpression containsMemberUsernameExpression(String keyword) {
        return communityCategory.type.ne(BLIND).and(community.member.username.contains(keyword));
    }

    private BooleanBuilder containsContents(String keyword) {
        return nullSafeBuilder(() ->  community.contents.contains(keyword));
    }

    private BooleanBuilder containsTitle(String keyword) {
        return nullSafeBuilder(() -> community.title.contains(keyword));
    }

    private BooleanExpression createdAtAfter(ZonedDateTime dateTime) {
        return dateTime == null ? null : community.createdAt.goe(dateTime);
    }

    private BooleanExpression createdAtBefore(ZonedDateTime dateTime) {
        return dateTime == null ? null : community.createdAt.loe(dateTime);
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
