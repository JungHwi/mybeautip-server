package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberDetailRepository;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberMemoRepository;
import com.jocoos.mybeautip.domain.member.vo.MemberBasicSearchResult;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchCondition;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class MemberDao {

    private final MemberRepository repository;
    private final MemberDetailRepository memberDetailRepository;
    private final MemberMemoRepository memberMemoRepository;

    @Transactional(readOnly = true)
    public Member getMember(long memberId) {
        return repository.getById(memberId);
    }
    
    @Transactional(readOnly = true)
    public Map<MemberStatus, Long> getStatusesWithCount() {
        return repository.getStatusesWithCount();
    }

    @Transactional(readOnly = true)
    public MemberSearchResult getMembersWithDetails(Long memberId) {
        return repository.getMemberWithDetails(memberId);
    }

    @Transactional(readOnly = true)
    public Long countInvitedFriends(Long memberId) {
        return memberDetailRepository.countByInviterId(memberId);
    }

    @Transactional
    public void updateMemberMemo(Long memberId, String memo) {
        Member member = getMember(memberId);
        memberMemoRepository.findByMember(member)
                .ifPresentOrElse(
                        memberMemo -> memberMemo.update(memo),
                        () -> saveMemo(member, memo));
    }

    private void saveMemo(Member member, String memo) {
        memberMemoRepository.save(new MemberMemo(memo, member));
    }

    @Transactional(readOnly = true)
    public Page<MemberBasicSearchResult> getMembers(MemberSearchCondition condition) {
        return repository.getMembers(condition);
    }
}
