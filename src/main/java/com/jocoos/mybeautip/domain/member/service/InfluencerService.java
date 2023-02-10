package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.converter.InfluencerConverter;
import com.jocoos.mybeautip.domain.member.dto.InfluencerRequest;
import com.jocoos.mybeautip.domain.member.dto.InfluencerResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer;
import com.jocoos.mybeautip.domain.member.service.dao.InfluencerDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InfluencerService {

    private final InfluencerDao dao;
    private final InfluencerConverter converter;

    @Transactional
    public InfluencerResponse updateInfluencer(long id, InfluencerRequest request) {
        Influencer influencer = dao.updateInfluencer(id, request);
        return converter.converts(influencer);
    }
}
