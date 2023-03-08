package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.code.InfluencerStatus;
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer;
import com.jocoos.mybeautip.domain.member.persistence.repository.InfluencerRepository;
import com.jocoos.mybeautip.global.util.EntityMapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    public List<Influencer> bulkUpdateInfluencer(List<Long> ids, InfluencerStatus status) {
        // TODO Member Exist Check Needed?

        List<Influencer> existInfluencerList = repository.findAllByIdIn(ids);
        Map<Long, Influencer> idInfluncerMap = EntityMapUtil.getIdEntityMap(existInfluencerList, Influencer::getId);
        List<Influencer> influencerList = ids.stream()
                .map(id -> {
                    Influencer influencer = idInfluncerMap.getOrDefault(id, new Influencer(id));
                    influencer.updateStatus(status);
                    return influencer;
                })
                .toList();

        return repository.saveAll(influencerList);
    }
}
