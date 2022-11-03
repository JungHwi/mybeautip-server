package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberDetailRepository;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class MemberDao {

    private final MemberRepository repository;
    private final MemberDetailRepository memberDetailRepository;

    @Transactional(readOnly = true)
    public Member getMember(long memberId) {
        return repository.getById(memberId);
    }
    
    @Transactional(readOnly = true)
    public Map<MemberStatus, Long> getStatusesWithCount() {
        return repository.getStatusesWithCount();
    }

    public MemberSearchResult getMembersWithDetails(Long memberId) {
        return repository.getMemberWithDetails(memberId);
    }

    @Transactional(readOnly = true)
    public Long countInvitedFriends(Long memberId) {
        return memberDetailRepository.countByInviterId(memberId);
    }

    @Transactional
    public void updateMemberMemo(Long memberId, String memo) {
        getMember(memberId).updateMemo(memo);
    }
}
