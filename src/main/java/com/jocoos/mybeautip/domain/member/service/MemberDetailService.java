package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberDetail;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberDetailRepository;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberDetailService {
    private final MemberDetailRepository repository;

    @Transactional(readOnly = true)
    public MemberDetail findById(long memberId) {
        return repository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException("No such member id - " + memberId));
    }

    @Transactional
    public void updateDetail(MemberDetail detail) {
        repository.save(detail);
    }

}
