package com.jocoos.mybeautip.member.comment.impl;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.member.block.BlockStatus;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentCustomRepository;
import com.jocoos.mybeautip.restapi.CommentSearchCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jocoos.mybeautip.member.QMember.member;
import static com.jocoos.mybeautip.member.block.QBlock.block;
import static com.jocoos.mybeautip.member.comment.QComment.comment1;

@Repository
public class CommentCustomRepositoryImpl implements CommentCustomRepository {
    private final ExtendedQuerydslJpaRepository<Comment, Long> repository;

    public CommentCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Comment, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<Comment> getComments(CommentSearchCondition condition, Pageable pageable) {
        JPAQuery<Comment> baseQuery = getBaseQuery(condition, pageable);
        dynamicQueryForLogin(baseQuery, condition);
        return baseQuery.fetch();
    }

    private JPAQuery<Comment> getBaseQuery(CommentSearchCondition condition, Pageable pageable) {
        return repository.query(query -> query
                .select(comment1)
                .from(comment1)
                .join(member).on(comment1.createdBy.id.eq(member.id))
                .where(
                        eqVideo(condition.videoId()),
                        eqState(condition.state()),
                        getParent(condition),
                        getCursor(condition, pageable)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortComment(pageable)));
    }

    private BooleanExpression eqState(Comment.CommentState state) {
        return state == null ? null : comment1.state.eq(state.value());
    }

    private OrderSpecifier<?> sortComment(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            return new OrderSpecifier<>(direction, comment1.id);
        }
        return null;
    }

    private BooleanExpression eqVideo(Long videoId) {
        return videoId == null ? null : comment1.videoId.eq(videoId);
    }

    private BooleanExpression getParent(CommentSearchCondition condition) {
        return condition.parentId() == null ?
                comment1.parentId.isNull() :
                comment1.parentId.eq(condition.parentId());
    }

    private BooleanExpression getCursor(CommentSearchCondition condition, Pageable pageable) {
        if (condition.cursor() == null) {
            return null;
        } else {
            return pageable.getSort().stream()
                    .anyMatch(Sort.Order::isAscending) ?
                    comment1.id.gt(condition.cursor()) :
                    comment1.id.lt(condition.cursor());
        }
    }

    private void dynamicQueryForLogin(JPAQuery<Comment> query, CommentSearchCondition condition) {
        if (condition.memberId() != null) {
            dynamicQueryForBlock(query, condition);
        }
    }

    private void dynamicQueryForBlock(JPAQuery<Comment> query, CommentSearchCondition condition) {
        query.leftJoin(block).on(comment1.createdBy.id.eq(block.memberYou.id).and(block.me.eq(condition.memberId())).and(block.status.eq(BlockStatus.BLOCK)))
                .where(block.memberYou.id.isNull());
    }
}


