package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberActivityCount;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberActivityCountRepository;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberActivityCountDao {

    private final MemberActivityCountRepository repository;

    @Transactional
    public void init(Member member) {
        repository.save(new MemberActivityCount(member));
    }

    @Transactional
    public void updateAllCommunityCount(Member member, int count) {
        repository.updateAllAndNormalCommunityCount(member, count);
    }

    @Transactional
    public void updateNormalCommunityCount(Member member, int count) {
        repository.updateNormalCommunityCount(member.getId(), count);
    }

    @Transactional
    public void updateAllCommunityCommentCount(Member member, int count) {
        repository.updateAllAndNormalCommunityCommentCount(member, count);
    }

    @Transactional
    public void updateAllVideoCommentCount(Member member, int count) {
        repository.updateAllAndNormalVideoCommentCount(member, count);
    }

    @Transactional
    public void updateNormalCommunityCommentCount(List<Long> ids, int count) {
        repository.updateNormalCommunityCommentCount(ids, count);
    }

    @Transactional
    public void updateNormalVideoCommentCount(List<Long> ids, int count) {
        repository.updateNormalVideoCommentCount(ids, count);
    }
}
