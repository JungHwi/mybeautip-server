package com.jocoos.mybeautip.domain.video.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.domain.video.vo.VideoSearchCondition;
import com.jocoos.mybeautip.video.Video;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static com.jocoos.mybeautip.video.QVideo.video;
import static com.jocoos.mybeautip.video.QVideoCategoryMapping.videoCategoryMapping;
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
                    .innerJoin(videoCategoryMapping).on(videoCategoryMapping.videoId.eq(video.id))
                    .where(eqCategoryId(condition.getCategoryId()))
                    .fetch();
        }
        return baseSearchQuery(condition).fetch();
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

    private BooleanExpression lessOrEqualThanCreatedAt(Date cursor) {
        return cursor == null ? null : video.createdAt.loe(cursor);
    }

    private BooleanExpression eqCategoryId(Integer categoryId) {
        return categoryId == null ? null : videoCategoryMapping.categoryId.eq(categoryId);
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
