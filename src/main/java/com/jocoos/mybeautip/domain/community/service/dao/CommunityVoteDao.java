package com.jocoos.mybeautip.domain.community.service.dao;

import com.jocoos.mybeautip.domain.community.persistence.domain.vote.CommunityVote;
import com.jocoos.mybeautip.domain.community.persistence.repository.CommunityVoteRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommunityVoteDao {

    private final CommunityVoteRepository repository;

    @Transactional(readOnly = true)
    public CommunityVote get(Long voteId) {
        return repository.findById(voteId)
                .orElseThrow(() -> new NotFoundException("No such community vote id. id - " + voteId));
    }

    @Transactional
    public void increaseVoteCount(Long voteId) {
        repository.voteCount(voteId, NumberUtils.INTEGER_ONE);
    }
}
