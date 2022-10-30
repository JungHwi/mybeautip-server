package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.member.Member;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.jocoos.mybeautip.member.QMember.member;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.sql.SQLExpressions.count;

@Repository
public class MemberCustomRepositoryImpl implements MemberCustomRepository {

    private final ExtendedQuerydslJpaRepository<Member, Long> repository;

    public MemberCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Member, Long> repository) {
        this.repository = repository;
    }

    @Override
    public Map<MemberStatus, Long> getStatusesWithCount() {
        return repository.query(query -> query
                .select(member.status, count(member))
                .from(member)
                .groupBy(member.status)
                .transform(groupBy(member.status).as(count(member))));
    }
}
