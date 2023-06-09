package com.jocoos.mybeautip.domain.member.service.dao;

import com.jocoos.mybeautip.domain.member.code.InfluencerStatus;
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer;
import com.jocoos.mybeautip.domain.member.persistence.repository.InfluencerRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public List<Influencer> bulkUpdateInfluencer(Set<Long> ids, InfluencerStatus status) {
        if (ids.size() != memberDao.countByIdIn(ids)) {
            throw new BadRequestException("member not exist id in " + ids);
        }

        List<Influencer> existInfluencerList = repository.findAllByIdIn(ids);
        Map<Long, Influencer> idInfluncerMap = EntityUtil.getIdEntityMap(existInfluencerList, Influencer::getId);
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
