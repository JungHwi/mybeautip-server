package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.member.persistence.domain.DormantMember;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo;
import com.jocoos.mybeautip.domain.member.persistence.repository.DormantMemberRepository;
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

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class MemberDao {

    private final MemberRepository repository;
    private final MemberDetailRepository memberDetailRepository;
    private final MemberMemoRepository memberMemoRepository;
    private final DormantMemberRepository dormantMemberRepository;
    private final MemberConverter memberConverter;

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

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Transactional
    public List<Member> getDormantTarget() {
        ZonedDateTime targetDate = ZonedDateTime.now().minusYears(1);
        return repository.findByStatusAndLastLoggedAtLessThan(MemberStatus.ACTIVE, targetDate);
    }

    @Transactional()
    public DormantMember changeToDormantMember(Member member) {
        DormantMember dormantMember = memberConverter.convertForDormant(member);
        return dormantMemberRepository.save(dormantMember);
    }
}
