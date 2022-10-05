package com.jocoos.mybeautip.domain.video.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.search.vo.KeywordSearchCondition;
import com.jocoos.mybeautip.domain.search.vo.SearchResult;
import com.jocoos.mybeautip.video.Video;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static com.jocoos.mybeautip.video.QVideo.video;
import static com.querydsl.sql.SQLExpressions.count;

@Repository
public class VideoCustomRepositoryImpl implements VideoCustomRepository {

    private final ExtendedQuerydslJpaRepository<Video, Long> repository;

    public VideoCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Video, Long> repository) {
        this.repository = repository;
    }


    @Override
    public SearchResult<Video> search(KeywordSearchCondition condition) {
        List<Video> videos = repository.query(query -> query
                .select(video)
                .from(video)
                .where(
                        searchCondition(condition.getKeyword()),
                        lessThanSortedAt(condition.cursorDate()),
                        eqVisibilityPublic(),
                        inState(Arrays.asList("LIVE", "VOD")),
                        video.deletedAt.isNull()
                )
                .limit(condition.getSize())
                .orderBy(video.id.desc())
                .fetch());

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

    private BooleanExpression lessThanSortedAt(Date cursor) {
        return cursor == null ? null : video.createdAt.lt(cursor);
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
