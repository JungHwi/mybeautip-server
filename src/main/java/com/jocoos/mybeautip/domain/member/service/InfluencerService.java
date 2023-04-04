package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.broadcast.event.BroadcastEvent.BroadcastForceFinishEvent;
import com.jocoos.mybeautip.domain.member.converter.InfluencerConverter;
import com.jocoos.mybeautip.domain.member.dto.InfluencerRequest;
import com.jocoos.mybeautip.domain.member.dto.InfluencerResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.Influencer;
import com.jocoos.mybeautip.domain.member.service.dao.InfluencerDao;
import com.jocoos.mybeautip.global.util.EntityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.member.code.InfluencerStatus.INACTIVE;

@Service
@RequiredArgsConstructor
public class InfluencerService {

    private final InfluencerDao dao;
    private final InfluencerConverter converter;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public List<InfluencerResponse> bulkUpdateInfluencer(InfluencerRequest request) {
        List<Influencer> influencerList = dao.bulkUpdateInfluencer(request.ids(), request.status());
        if (INACTIVE.equals(request.status())) {
            List<Long> memberIds = EntityUtil.extractLongList(influencerList, Influencer::getId);
            eventPublisher.publishEvent(new BroadcastForceFinishEvent(memberIds));
        }
        return converter.converts(influencerList);
    }
}
