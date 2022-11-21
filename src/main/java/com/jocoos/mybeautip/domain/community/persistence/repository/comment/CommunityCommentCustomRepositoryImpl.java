package com.jocoos.mybeautip.domain.community.persistence.repository.comment;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.community.code.CommunityStatus;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.vo.CommunityCommentSearchCondition;
import com.jocoos.mybeautip.member.block.BlockStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;
import static com.jocoos.mybeautip.domain.community.code.CommunityStatus.NORMAL;
import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunityCategory.communityCategory;
import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunityComment.communityComment;
import static com.jocoos.mybeautip.member.block.QBlock.block;

@Repository
public class CommunityCommentCustomRepositoryImpl implements CommunityCommentCustomRepository {

    private final ExtendedQuerydslJpaRepository<CommunityComment, Long> repository;


    public CommunityCommentCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<CommunityComment, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<CommunityComment> getComments(CommunityCommentSearchCondition condition, Pageable pageable) {
        JPAQuery<CommunityComment> query = getBaseQuery(condition, pageable);
        dynamicQueryForLogin(query, condition);
        return query.fetch();
    }

    @Override
    public void updateStatusIdIn(List<Long> ids, CommunityStatus status) {
         repository.update(query -> query
                .set(communityComment.status, status)
                .where(inId(ids))
                .execute());
    }

    private JPAQuery<CommunityComment> getBaseQuery(CommunityCommentSearchCondition condition, Pageable pageable) {
        boolean direction = isIdAscending(pageable);

        return repository.query(query -> query
                .select(communityComment)
                .from(communityComment)
                .where(
                        eqCommunityId(condition.communityId()),
                        eqParentId(condition.parentId()),
                        greaterOrLessThanIdByDirection(direction, condition.cursor()),
                        eqStatus(NORMAL)
                )
                .orderBy(orderById(direction))
                .limit(pageable.getPageSize()));
    }

    private void dynamicQueryForLogin(JPAQuery<CommunityComment> query, CommunityCommentSearchCondition condition) {
        if (condition.memberId() != null) {
            dynamicQueryForBlock(query, condition);
        }
    }

    private void dynamicQueryForBlock(JPAQuery<CommunityComment> query, CommunityCommentSearchCondition condition) {
        query.innerJoin(communityCategory).on(communityComment.categoryId.eq(communityCategory.id))
                .leftJoin(block).on(communityComment.member.id.eq(block.memberYou.id).and(block.me.eq(condition.memberId())).and(block.status.eq(BlockStatus.BLOCK)))
                .where(communityCategory.type.eq(BLIND).or(block.memberYou.id.isNull().and(communityCategory.type.ne(BLIND))));
    }

    private BooleanExpression eqStatus(CommunityStatus status) {
        return status == null ? null : communityComment.status.eq(status);
    }

    private BooleanExpression eqCommunityId(long communityId) {
        return communityComment.communityId.eq(communityId);
    }

    private BooleanExpression inId(List<Long> ids) {
        return communityComment.id.in(ids);
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
