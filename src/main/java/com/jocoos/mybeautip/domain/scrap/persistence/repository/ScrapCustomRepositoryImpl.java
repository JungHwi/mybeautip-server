package com.jocoos.mybeautip.domain.scrap.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.vo.ScrapSearchCondition;
import com.jocoos.mybeautip.member.block.BlockStatus;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.BLIND;
import static com.jocoos.mybeautip.domain.community.persistence.domain.QCommunity.community;
import static com.jocoos.mybeautip.domain.scrap.persistence.domain.QScrap.scrap;
import static com.jocoos.mybeautip.member.block.QBlock.block;

@Repository
public class ScrapCustomRepositoryImpl implements ScrapCustomRepository {


    private final ExtendedQuerydslJpaRepository<Scrap, Long> repository;

    public ScrapCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Scrap, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<Scrap> getScrapsExcludeBlockMember(ScrapSearchCondition condition) {
        JPAQuery<Scrap> baseQuery = repository.query(query -> query
                .select(scrap)
                .from(scrap)
                .where(
                        eqType(condition.type()),
                        eqMemberId(condition.memberId()),
                        ltId(condition.cursor()),
                        isScrap(condition.isScrap())
                )
                .limit(condition.limit())
                .orderBy(getOrder(condition.sort())));

        dynamicQueryForBlock(baseQuery, condition.memberId());

        return baseQuery.fetch();
    }

    private BooleanExpression eqType(ScrapType type) {
        return type == null ? null : scrap.type.eq(type);
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return memberId == null ? null : scrap.memberId.eq(memberId);
    }

    private BooleanExpression ltId(Long cursor) {
        return cursor == null ? null : scrap.id.lt(cursor);
    }

    private BooleanExpression isScrap(Boolean isScrap) {
        return isScrap == null ? null : scrap.isScrap.eq(isScrap);
    }

    private OrderSpecifier<?> getOrder(Sort sort) {
        return sort.stream()
                .findFirst()
                .map(order -> {
                    Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                    SimplePath<Scrap> path = Expressions.path(Scrap.class, scrap, order.getProperty());
                    return new OrderSpecifier(direction, path);
                })
                .orElse(scrap.id.desc());
    }

    private void dynamicQueryForBlock(JPAQuery<?> query, Long memberId) {
        query
                .join(community).on(scrap.relationId.eq(community.id))
                .leftJoin(block).on(community.member.id.eq(block.memberYou.id).and(block.me.eq(memberId)).and(block.status.eq(BlockStatus.BLOCK)))
                .where(community.category.type.eq(BLIND).or(block.memberYou.id.isNull().and(community.category.type.ne(BLIND))));
    }
}
