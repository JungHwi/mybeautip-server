package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.code.InfluencerStatus;
import com.jocoos.mybeautip.domain.member.dto.InfluencerRequest;
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer;
import com.jocoos.mybeautip.domain.member.persistence.repository.InfluencerRepository;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InfluencerDao {

    private final MemberDao memberDao;
    private final InfluencerRepository repository;

    @Transactional(readOnly = true)
    public boolean isInfluencer(long memberId) {
        return repository.existsByIdAndStatus(memberId, InfluencerStatus.ACTIVE);
    }

    @Transactional
    public Influencer updateInfluencer(long id, InfluencerRequest request) {
        if (!memberDao.existMember(id)) {
            throw new MemberNotFoundException(id);
        }

        Influencer influencer = repository.findById(id)
                .orElse(new Influencer(id));

        influencer.updateStatus(request.status());

        return repository.save(influencer);
    }
}
