package com.jocoos.mybeautip.domain.video.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.video.dto.AdminVideoResponse;
import com.jocoos.mybeautip.domain.video.dto.QAdminVideoResponse;
import com.jocoos.mybeautip.domain.video.vo.AdminVideoSearchCondition;
import com.jocoos.mybeautip.domain.video.vo.VideoSearchCondition;
import com.jocoos.mybeautip.global.vo.SearchOption;
import com.jocoos.mybeautip.video.Video;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static com.jocoos.mybeautip.domain.video.persistence.domain.QVideoCategory.videoCategory;
import static com.jocoos.mybeautip.global.code.SearchField.COMMENT;
import static com.jocoos.mybeautip.member.QMember.member;
import static com.jocoos.mybeautip.member.comment.QComment.comment1;
import static com.jocoos.mybeautip.video.QVideo.video;
import static com.jocoos.mybeautip.video.QVideoCategoryMapping.videoCategoryMapping;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.sql.SQLExpressions.count;

@Repository
public class VideoCustomRepositoryImpl implements VideoCustomRepository {

    private final ExtendedQuerydslJpaRepository<Video, Long> repository;

    public VideoCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Video, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<Video> getVideos(VideoSearchCondition condition) {
        if (condition.isCategorySearch()) {
            return baseSearchQuery(condition)
                    .where(eqCategoryId(condition.getCategoryId()))
                    .fetch();
        }
        return baseSearchQuery(condition).fetch();
    }

    @Override
    public Page<AdminVideoResponse> getVideos(AdminVideoSearchCondition condition) {
        List<AdminVideoResponse> contents = getContents(condition);
        Long count = getCount(condition);
        return new PageImpl<>(contents, condition.pageable(), count);
    }

    private List<AdminVideoResponse> getContents(AdminVideoSearchCondition condition) {
        JPAQuery<?> query = baseSearchQuery(condition);
        return getContents(condition, query);
    }

    private Long getCount(AdminVideoSearchCondition condition) {
        Long count = baseSearchQuery(condition)
                .select(video.countDistinct())
                .fetchOne();
        return count == null ? 0 : count;
    }

    private List<AdminVideoResponse> getContents(AdminVideoSearchCondition condition, JPAQuery<?> query) {
        return query
                .select(new QAdminVideoResponse(video, member, GroupBy.list(videoCategory)))
                .orderBy(getOrders(condition.sort()))
                .offset(condition.offset())
                .limit(condition.limit())
                .transform(groupBy(video).list(new QAdminVideoResponse(video, member, GroupBy.list(videoCategory))));
    }

    private JPAQuery<?> baseSearchQuery(AdminVideoSearchCondition condition) {
        JPAQuery<?> baseQuery = repository.query(query -> query
                .from(video)
                .join(member).on(video.member.eq(member))
                .join(videoCategoryMapping).on(video.categoryMapping.contains(videoCategoryMapping))
                .join(videoCategory).on(videoCategoryMapping.videoCategory.eq(videoCategory))
                .where(
                        searchInnerField(condition.searchOption()),
                        eqCategoryId(condition.categoryId()),
                        goeCreatedAt(condition.startAtDate()),
                        loeCreatedAt(condition.endAtDate()),
                        isReported(condition.isReported())
                ));

        dynamicQueryForCommentSearch(condition, baseQuery);
        return baseQuery;
    }

    private void dynamicQueryForCommentSearch(AdminVideoSearchCondition condition, JPAQuery<?> baseQuery) {
        if (condition.isSearchFieldEqual(COMMENT)) {
            baseQuery
                    .distinct()
                    .leftJoin(comment1).on(comment1.videoId.eq(video.id))
                    .where(comment1.comment.containsIgnoreCase(condition.keyword()));
        }
    }

    private OrderSpecifier<?> getOrders(Sort sort) {
        return sort.stream()
                .findFirst()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    SimplePath<Video> path = Expressions.path(Video.class, video, order.getProperty());
                    return new OrderSpecifier(direction, path);
                })
                .orElse(video.createdAt.desc());
    }

    private BooleanExpression searchInnerField(SearchOption searchOption) {
        if (searchOption == null || searchOption.isNoSearch() || searchOption.isOuterField()) {
            return null;
        }
        return Expressions.booleanOperation(
                Ops.STRING_CONTAINS_IC,
                Expressions.path(String.class, video, searchOption.getSearchField()),
                Expressions.constant(searchOption.getKeyword()));
    }

    private BooleanExpression isReported(Boolean isReported) {
        if (isReported == null) {
            return null;
        }
        return isReported ? video.reportCount.gt(0) : video.reportCount.eq(0L);
    }


    @Override
    public SearchResult<Video> search(VideoSearchCondition condition) {
        List<Video> videos = baseSearchQuery(condition).fetch();
        return new SearchResult<>(videos, countBy(condition.getKeyword()));
    }

    @Override
    public Long countBy(String keyword) {
        return repository.query(query -> query
                .select(count(video))
                .from(video)
                .join(member).on(video.member.eq(member))
                .join(videoCategoryMapping).on(video.categoryMapping.contains(videoCategoryMapping))
                .join(videoCategory).on(videoCategoryMapping.videoCategory.eq(videoCategory))
                .where(
                        searchCondition(keyword),
                        eqVisibilityPublic(),
                        inState(Arrays.asList("LIVE", "VOD")),
                        video.deletedAt.isNull()
                )
                .fetchOne());
    }

    private JPAQuery<Video> baseSearchQuery(VideoSearchCondition condition) {
        return repository.query(query -> query
                .select(video)
                .from(video)
                .join(video.member, member).fetchJoin()
                .join(video.categoryMapping, videoCategoryMapping).fetchJoin()
                .join(videoCategoryMapping.videoCategory, videoCategory).fetchJoin()
                .where(
                        searchCondition(condition.getKeyword()),
                        lessOrEqualThanCreatedAt(condition.getCursor()),
                        eqVisibilityPublic(),
                        inState(Arrays.asList("LIVE", "VOD")),
                        video.deletedAt.isNull()
                )
                .limit(condition.getSize())
                .orderBy(video.createdAt.desc()));
    }

    private BooleanExpression loeCreatedAt(Date endAt) {
        return endAt == null ? null : video.createdAt.loe(endAt);
    }

    private BooleanExpression goeCreatedAt(Date startAt) {
        return startAt == null ? null : video.createdAt.goe(startAt);
    }

    private BooleanExpression lessOrEqualThanCreatedAt(Date cursor) {
        return cursor == null ? null : video.createdAt.loe(cursor);
    }

    private BooleanExpression eqCategoryId(Integer categoryId) {
        return categoryId == null ? null : videoCategoryMapping.videoCategory.id.eq(categoryId);
    }

    private BooleanExpression eqVisibilityPublic() {
        return video.visibility.eq("PUBLIC");
    }

    private BooleanExpression inState(List<String> states) {
        return CollectionUtils.isEmpty(states) ? null : video.state.in(states);
    }

    private BooleanBuilder searchCondition(String keyword) {
        return containsTitle(keyword).or(containsDescription(keyword));
    }

    private BooleanBuilder containsDescription(String keyword) {
        return nullSafeBuilder(() -> video.content.contains(keyword));
    }

    private BooleanBuilder containsTitle(String keyword) {
        return nullSafeBuilder(() -> video.title.contains(keyword));
    }

    private static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (NullPointerException e) {
            return new BooleanBuilder();
        }
    }
}
