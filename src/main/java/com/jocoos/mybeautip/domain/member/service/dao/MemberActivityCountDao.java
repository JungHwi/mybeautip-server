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
    public void updateCommunityCount(Long memberId, int count) {
        repository.updateCommunityCount(memberId, count);
    }

    @Transactional
    public void updateCommunityCommentCount(Long memberId, int count) {
        repository.updateCommunityCommentCount(memberId, count);
    }

    @Transactional
    public void updateVideoCommentCount(Long memberId, int count) {
        repository.updateVideoCommentCount(memberId, count);
    }

}
