package com.jocoos.mybeautip.domain.community.persistence.repository.comment;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.vo.CommunityCommentSearchCondition;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunityComment.communityComment;

@Repository
public class CommunityCommentCustomRepositoryImpl implements CommunityCommentCustomRepository {

    private final ExtendedQuerydslJpaRepository<CommunityComment, Long> repository;


    public CommunityCommentCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<CommunityComment, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<CommunityComment> getComments(CommunityCommentSearchCondition condition, Pageable pageable) {
        boolean direction = isIdAscending(pageable);
        return repository.query(query -> query
                .select(communityComment)
                .from(communityComment)
                .where(
                        eqCommunityId(condition.getCommunityId()),
                        eqParentId(condition.getParentId()),
                        greaterOrLessThanIdByDirection(direction, condition.getCursor())
                )
                .orderBy(orderById(direction))
                .limit(pageable.getPageSize())
                .fetch());
    }

    private BooleanExpression eqCommunityId(long communityId) {
        return communityComment.communityId.eq(communityId);
    }

    private OrderSpecifier<?> orderById(boolean isAscending) {
        return isAscending ? communityComment.id.asc() : communityComment.id.desc();
    }

    private BooleanExpression greaterOrLessThanIdByDirection(boolean isAscending, Long cursor) {
        if (cursor == null) {
            return null;
        }
        return isAscending ? communityComment.id.gt(cursor) : communityComment.id.lt(cursor);
    }

    private BooleanExpression eqParentId(Long parentId) {
        return parentId == null ? communityComment.parentId.isNull() : communityComment.parentId.eq(parentId);
    }

    private boolean isIdAscending(Pageable pageable) {
        final String property = "id";
        return pageable.getSort()
                .stream()
                .filter(order -> property.equals(order.getProperty()))
                .anyMatch(Sort.Order::isAscending);
    }
}
