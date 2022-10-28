package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;
import com.jocoos.mybeautip.member.Member;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberCustomRepositoryImpl implements MemberCustomRepository {


    private final ExtendedQuerydslJpaRepository<Member, Long> repository;


    public MemberCustomRepositoryImpl(@Lazy ExtendedQuerydslJpaRepository<Member, Long> repository) {
        this.repository = repository;
    }

    @Override
    public List<MemberStatusResponse> getStatusesWithCount() {
        return null;
    }
}
