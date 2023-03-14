package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.member.dto.MemberRegistrationRequest;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberDetailRepository;
import com.jocoos.mybeautip.domain.member.vo.MemberBasicSearchResult;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchCondition;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.support.DateUtils;
import com.jocoos.mybeautip.support.RandomUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberDao {

    private final MemberRepository repository;
    private final MemberDetailRepository memberDetailRepository;
    private final MemberConverter memberConverter;

    @Transactional(readOnly = true)
    public List<Member> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Member getMember(long memberId) {
        return repository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Not found member info. id - " + memberId));
    }

    @Transactional(readOnly = true)
    public boolean existMember(long memberId) {
        return repository.existsById(memberId);
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

    @Transactional(readOnly = true)
    public Page<MemberBasicSearchResult> getMembers(MemberSearchCondition condition) {
        return repository.getMembers(condition);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByTag(String tag) {
        return repository.existsByTag(tag);
    }

    @Transactional(readOnly = true)
    public List<Member> getDormantTarget() {
        ZonedDateTime targetDate = ZonedDateTime.now().minusYears(1).plusDays(1).truncatedTo(ChronoUnit.DAYS);
        return repository.findByStatusAndLastLoggedAtLessThan(MemberStatus.ACTIVE, targetDate);
    }

    @Transactional(readOnly = true)
    public List<Member> getDormantNotificationTarget() {
        ZonedDateTime lastYear = ZonedDateTime.now().minusYears(1).truncatedTo(ChronoUnit.DAYS);
        List<Member> memberList = repository.findByStatusAndLastLoggedAtGreaterThanEqualAndLastLoggedAtLessThan(MemberStatus.ACTIVE, lastYear.plusDays(30), lastYear.plusDays(31));
        memberList.addAll(repository.findByStatusAndLastLoggedAtGreaterThanEqualAndLastLoggedAtLessThan(MemberStatus.ACTIVE, lastYear.plusDays(7), lastYear.plusDays(8)));
        memberList.addAll(repository.findByStatusAndLastLoggedAtGreaterThanEqualAndLastLoggedAtLessThan(MemberStatus.ACTIVE, lastYear.plusDays(1), lastYear.plusDays(2)));
        return memberList;
    }

    @Transactional
    public List<Member> getSuspendedTarget() {
        ZonedDateTime targetDate = ZonedDateTime.now().minusWeeks(2);
        Date date = DateUtils.toDate(targetDate);
        return repository.findByStatusAndModifiedAtLessThan(MemberStatus.SUSPENDED, date);
    }

    @Transactional
    public Member updateStatus(Member member, MemberStatus status) {
        return member.changeStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Member> getMembers(Set<Long> ids) {
        return repository.findByIdIn(ids);
    }

    @Transactional(readOnly = true)
    public long countByIdIn(Set<Long> ids) {
        return repository.countByIdIn(ids);
    }

    @Transactional
    public Member saveOrUpdate(MemberRegistrationRequest request) {
        Optional<Member> exist = repository.findById(request.getId());
        if (exist.isPresent()) {
            Member member = exist.get();
            log.debug("{}", member);

            if (StringUtils.hasText(request.getUsername())) {
                member.setUsername(request.getUsername());
            }
            if (StringUtils.hasText(request.getAvatarUrl())) {
                member.setAvatarFilename(request.getAvatarUrl());
            }
            return repository.save(member);
        }

        repository.insert(request.getId(), request.getUsername(), request.getAvatarUrl(), RandomUtils.generateTag());
        return getMember(request.getId());
    }

}
