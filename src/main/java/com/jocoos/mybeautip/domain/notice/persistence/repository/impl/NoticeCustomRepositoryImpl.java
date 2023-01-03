package com.jocoos.mybeautip.domain.notice.persistence.repository.impl;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.notice.code.NoticeStatus;
import com.jocoos.mybeautip.domain.notice.dto.SearchNoticeRequest;
import com.jocoos.mybeautip.domain.notice.persistence.domain.Notice;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.domain.notice.persistence.domain.QNotice.notice;
import static com.jocoos.mybeautip.member.QMember.member;

@Repository
public class NoticeCustomRepositoryImpl implements NoticeCustomRepository {

    private final ExtendedQuerydslJpaRepository<Notice, Long> repository;

    public NoticeCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Notice, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Page<Notice> search(SearchNoticeRequest request) {
        JPAQuery<Notice> baseQuery = searchBaseQuery(request)
                .select(notice)
                .orderBy(orderBy(request.getPageable()))
                .limit(request.getPageable().getPageSize())
                .offset(request.getPageable().getOffset());

        List<Notice> noticeList = baseQuery.fetch();
        Long totalCount = countBySearch(request);

        return new PageImpl<>(noticeList, request.getPageable(), totalCount);
    }

    private Long countBySearch(SearchNoticeRequest request) {
        return searchBaseQuery(request)
                .select(notice.count())
                .fetchOne();
    }

    private JPAQuery<?> searchBaseQuery(SearchNoticeRequest request) {
        return repository.query(query -> query
                .from(notice)
                .innerJoin(member).on(notice.createdBy.id.eq(member.id))
                .where(
                        eqStatus(request.getStatus()),
                        likeTitle(request.getSearch()),
                        afterStartAt(request.getStartAt()),
                        beforeEndAt(request.getEndAt())
                )
        );
    }

    private BooleanExpression eqStatus(NoticeStatus status) {
        return status == null ? null : notice.status.eq(status);
    }

    private BooleanExpression isVisible(Boolean isVisible) {
        return isVisible == null ? null : notice.isVisible.eq(isVisible);
    }

    private BooleanExpression likeTitle(String search) {
        return StringUtils.isBlank(search) ? null : notice.title.like(search);
    }

    private BooleanExpression afterStartAt(ZonedDateTime startAt) {
        return startAt == null ? null : notice.createdAt.after(startAt);
    }

    private BooleanExpression beforeEndAt(ZonedDateTime endAt) {
        return endAt == null ? null : notice.createdAt.before(endAt);
    }

    private OrderSpecifier<?>[] orderBy(Pageable pageable) {
        if (pageable == null) {
            return null;
        }

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        PathBuilder<Notice> pathBuilder = new PathBuilder<>(Notice.class, "notice");
        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();
            orderSpecifiers.add(new OrderSpecifier(direction, pathBuilder.get(property)));
        }

        return orderSpecifiers.stream().toArray(OrderSpecifier[]::new);
    }
}