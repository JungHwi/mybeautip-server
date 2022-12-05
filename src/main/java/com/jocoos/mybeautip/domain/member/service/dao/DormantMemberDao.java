package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.converter.DormantMemberConverter;
import com.jocoos.mybeautip.domain.member.persistence.domain.DormantMember;
import com.jocoos.mybeautip.domain.member.persistence.repository.DormantMemberRepository;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DormantMemberDao {
    private final DormantMemberRepository repository;
    private final DormantMemberConverter converter;

    @Transactional()
    public DormantMember changeToDormantMember(Member member) {
        DormantMember dormantMember = converter.convertForDormant(member);
        return repository.save(dormantMember);
    }

    @Transactional(readOnly = true)
    public DormantMember getDormantMember(long memberId) {
        return repository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND, "Not found dormant info. Member id is " + memberId));
    }

    @Transactional
    public void deleteDormantMember(DormantMember dormantMember) {
        repository.delete(dormantMember);
    }
}