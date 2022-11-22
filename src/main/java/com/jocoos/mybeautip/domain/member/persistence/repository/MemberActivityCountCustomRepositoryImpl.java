package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberActivityCount;
import com.jocoos.mybeautip.member.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import static com.jocoos.mybeautip.domain.member.persistence.domain.QMemberActivityCount.memberActivityCount;

@Repository
public class MemberActivityCountCustomRepositoryImpl implements MemberActivityCountCustomRepository {

    private final ExtendedQuerydslJpaRepository<MemberActivityCount, Long> repository;

    public MemberActivityCountCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<MemberActivityCount, Long> repository) {
        this.repository = repository;
    }

    @Override
    public void updateAllAndNormalCommunityCount(Member member, int count) {
        repository.update(query -> query
                .set(memberActivityCount.communityCount, memberActivityCount.communityCount.add(count))
                .set(memberActivityCount.allCommunityCount, memberActivityCount.allCommunityCount.add(count))
                .where(eqMember(member))
                .execute());
    }

    @Override
    public void updateAllAndNormalCommunityCommentCount(Member member, int count) {
        repository.update(query -> query
                .set(memberActivityCount.communityCommentCount, memberActivityCount.communityCommentCount.add(count))
                .set(memberActivityCount.allCommunityCommentCount, memberActivityCount.allCommunityCommentCount.add(count))
                .where(eqMember(member))
                .execute());
    }

    @Override
    public void updateAllAndNormalVideoCommentCount(Member member, int count) {
        repository.update(query -> query
                .set(memberActivityCount.videoCommentCount, memberActivityCount.videoCommentCount.add(count))
                .set(memberActivityCount.allVideoCommentCount, memberActivityCount.allVideoCommentCount.add(count))
                .where(eqMember(member))
                .execute());
    }

    private static BooleanExpression eqMember(Member member) {
        return member == null ? null : memberActivityCount.member.eq(member);
    }
}
