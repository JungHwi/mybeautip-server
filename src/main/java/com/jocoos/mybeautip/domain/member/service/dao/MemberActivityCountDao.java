package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberActivityCount;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberActivityCountRepository;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberActivityCountDao {

    private final MemberActivityCountRepository repository;

    @Transactional
    public void initActivityCount(Member member) {
        repository.save(new MemberActivityCount(member));
    }

    @Transactional
    public void plusCommunityCount(Long memberId) {
        repository.updateCommunityCount(memberId, 1);
    }

    @Transactional
    public void plusCommunityCommentCount(Long memberId) {
        repository.updateCommunityCommentCount(memberId, 1);
    }

    @Transactional
    public void plusVideoCommentCount(Long memberId) {
        repository.updateCommunityCommentCount(memberId, 1);
    }

    @Transactional
    public void subCommunityCount(Long memberId) {
        repository.updateCommunityCount(memberId, -1);
    }

    @Transactional
    public void subCommunityCommentCount(Long memberId) {
        repository.updateCommunityCommentCount(memberId, -1);
    }

    @Transactional
    public void subVideoCommentCount(Long memberId) {
        repository.updateCommunityCommentCount(memberId, -1);
    }

}
