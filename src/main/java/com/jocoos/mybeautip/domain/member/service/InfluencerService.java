package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.converter.InfluencerConverter;
import com.jocoos.mybeautip.domain.member.dto.InfluencerRequest;
import com.jocoos.mybeautip.domain.member.dto.InfluencerResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer;
import com.jocoos.mybeautip.domain.member.service.dao.InfluencerDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InfluencerService {

    private final InfluencerDao dao;
    private final InfluencerConverter converter;

    @Transactional
    public List<InfluencerResponse> bulkUpdateInfluencer(InfluencerRequest request) {
        List<Influencer> influencerList = dao.bulkUpdateInfluencer(request.ids(), request.status());
        return converter.converts(influencerList);
    }
}
